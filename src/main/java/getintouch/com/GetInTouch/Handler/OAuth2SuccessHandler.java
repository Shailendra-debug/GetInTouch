package getintouch.com.GetInTouch.Handler;


import getintouch.com.GetInTouch.Entity.User.RefreshToken;
import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.RefreshTokenRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import getintouch.com.GetInTouch.Service.Auth.CookieService;
import getintouch.com.GetInTouch.Util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final JwtUtil jwtUtil;


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Save user if not exists
        User user;
        if (userRepository.existsByEmail(email)){
             user=userRepository.findByEmail(email).orElseThrow();
        }else {
            user=new User();
            user.setEmail(email);
            user.setPassword(generatePassword());
            user.setRole(Role.USER);
            user.setFullName(name);
            user.setUsername(email);
            userRepository.save(user);
        }
        // ✅ Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // ✅ Save refresh token
        saveRefreshToken(user, refreshToken);

        // ✅ Use CookieService (clean + secure)
        cookieService.attachAccessCookie(response, accessToken);
        cookieService.attachRefreshCookie(response, refreshToken);

        // ✅ Redirect to frontend
        response.sendRedirect(frontendUrl);
    }

    public static String generatePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*";

        String allChars = upper + lower + digits + symbols;
        Random rand = new Random();

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upper.charAt(rand.nextInt(upper.length())));
        password.append(lower.charAt(rand.nextInt(lower.length())));
        password.append(digits.charAt(rand.nextInt(digits.length())));
        password.append(symbols.charAt(rand.nextInt(symbols.length())));

        // Fill remaining 4 characters randomly
        for (int i = 4; i < 8; i++) {
            password.append(allChars.charAt(rand.nextInt(allChars.length())));
        }

        return password.toString();
    }
    private void saveRefreshToken(User user, String tokenValue) {

        RefreshToken token = new RefreshToken();

        token.setUser(user);
        token.setToken(tokenValue);
        token.setExpiryDate(jwtUtil.getRefreshExpiry());

        refreshTokenRepository.save(token);
    }
}