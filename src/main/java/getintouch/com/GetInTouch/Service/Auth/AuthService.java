package getintouch.com.GetInTouch.Service.Auth;

import getintouch.com.GetInTouch.DTO.Auth.LoginRequestDTO;
import getintouch.com.GetInTouch.DTO.Auth.LoginResponseDto;
import getintouch.com.GetInTouch.Entity.User.RefreshToken;
import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Exception.UnauthorizedException;
import getintouch.com.GetInTouch.Repository.RefreshTokenRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import getintouch.com.GetInTouch.Util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /* ---------------- LOGIN ---------------- */

    public LoginResponseDto login(LoginRequestDTO request, HttpServletResponse response) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // 🍪 Cookies (optional but recommended for web)
        addCookie(response, "ACCESS_TOKEN", accessToken, 15 * 60);
        addCookie(response, "REFRESH_TOKEN", refreshToken, 7 * 24 * 60 * 60);

        saveRefreshToken(user, refreshToken);

        // 📦 Response body
        LoginResponseDto dto = new LoginResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);

        return dto;
    }

    /* ---------------- REFRESH TOKEN ---------------- */

    public LoginResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String oldRefreshToken = extractTokenFromCookie(request, "REFRESH_TOKEN");

        if (oldRefreshToken == null) {
            throw new UnauthorizedException("Refresh token missing");
        }

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(oldRefreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = storedToken.getUser();

        // 🔁 ROTATION
        refreshTokenRepository.delete(storedToken);

        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        saveRefreshToken(user, newRefreshToken);

        addCookie(response, "ACCESS_TOKEN", newAccessToken, 15 * 60);
        addCookie(response, "REFRESH_TOKEN", newRefreshToken, 7 * 24 * 60 * 60);

        // 📦 Response body
        LoginResponseDto dto = new LoginResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setAccessToken(newAccessToken);
        dto.setRefreshToken(newRefreshToken);

        return dto;
    }

    /* ---------------- LOGOUT ---------------- */

    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = extractTokenFromCookie(request, "REFRESH_TOKEN");

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }

        clearCookie(response, "ACCESS_TOKEN");
        clearCookie(response, "REFRESH_TOKEN");
    }

    /* ---------------- HELPERS ---------------- */

    private void saveRefreshToken(User user, String tokenValue) {

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        RefreshToken token;

        if (existingToken.isPresent()) {
            token = existingToken.get();   // update existing
        } else {
            token = new RefreshToken();    // create new
            token.setUser(user);
        }

        token.setToken(tokenValue);
        token.setExpiryDate(jwtUtil.getRefreshExpiry());

        refreshTokenRepository.save(token);
    }

    private String extractTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void addCookie(HttpServletResponse response, String name,
                           String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in prod
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in prod
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
