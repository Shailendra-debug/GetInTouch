package getintouch.com.GetInTouch.Service.Auth;

import getintouch.com.GetInTouch.DTO.Auth.*;
import getintouch.com.GetInTouch.Entity.User.PasswordResetOtp;
import getintouch.com.GetInTouch.Entity.User.RefreshToken;
import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Exception.UnauthorizedException;
import getintouch.com.GetInTouch.Repository.PasswordResetOtpRepository;
import getintouch.com.GetInTouch.Repository.RefreshTokenRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import getintouch.com.GetInTouch.Util.JwtUtil;
import getintouch.com.GetInTouch.Util.OtpUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
    private final EmailService emailService;
    private final OtpUtil otpUtil;
    private final PasswordResetOtpRepository otpRepo;

    private static final int MAX_ATTEMPTS = 5;

    /* ---------------- LOGIN ---------------- */

    public LoginResponseDto login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /* ---------------- REFRESH TOKEN ---------------- */

    public LoginResponseDto refreshToken(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = storedToken.getUser();

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())
                .build();
    }

    /* ---------------- LOGOUT ---------------- */

    public void logout(String refreshToken) {

        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
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

    public ForgotPasswordResponseDto sendOtp(ForgotPasswordRequestDto request) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (!userRepository.existsByEmail(request.getEmail()))
            return ForgotPasswordResponseDto.builder()
                .otpSent(false) // always true (security)
                .build();

        // avoid email enumeration
        if (userOpt.isEmpty()) {
            return ForgotPasswordResponseDto.builder()
                    .otpSent(true) // always true (security)
                    .build();
        }

        String email = request.getEmail();

        otpRepo.findTopByEmailOrderByCreatedAtDesc(email).ifPresent(existing -> {
            if (existing.getCreatedAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Too many requests. Try later.");
            }
        });

        String otp = otpUtil.generateOtp();

        PasswordResetOtp entity = PasswordResetOtp.builder()
                .email(email)
                .otpHash(passwordEncoder.encode(otp))
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        otpRepo.save(entity);

        emailService.sendOtp(email, otp);

        return ForgotPasswordResponseDto.builder()
                .otpSent(true)
                .build();
    }

    // ✅ RESET PASSWORD
    @Transactional
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto request) {

        PasswordResetOtp otpEntity = otpRepo
                .findTopByEmailOrderByCreatedAtDesc(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid request"));

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (otpEntity.getAttemptCount() >= MAX_ATTEMPTS) {
            throw new RuntimeException("Too many failed attempts");
        }

        if (!passwordEncoder.matches(request.getOtp(), otpEntity.getOtpHash())) {

            otpEntity.setAttemptCount(otpEntity.getAttemptCount() + 1);
            otpRepo.save(otpEntity);

            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpRepo.deleteByEmail(request.getEmail());

        return ResetPasswordResponseDto.builder()
                .passwordReset(true)
                .build();
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

        String cookie = name + "=" + value +
                "; Max-Age=" + maxAge +
                "; Path=/" +
                "; Domain=getintouch-mk7b.onrender.com" + // ✅ FIX
                "; HttpOnly" +
                "; Secure" +
                "; SameSite=None";

        response.addHeader("Set-Cookie", cookie); // ✅ addHeader use karo
    }


    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // true in prod
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
