package getintouch.com.GetInTouch.Service.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CookieService {

    @Value("${jwt.refresh-token-cookie-name:RefreshToken}")
    private String refreshTokenCookieName;

    @Value("${jwt.cookie-http-only:true}")
    private boolean cookieHttpOnly;

    @Value("${jwt.refresh-ttl-seconds:604800}")
    private long refreshTtl;

    @Value("${jwt.access-token-cookie-name:AccessToken}")
    private String accessTokenCookieName;

    // ✅ Attach Refresh Token
    public void attachRefreshCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(false) // 🔥 MUST for localhost
                .path("/")
                .maxAge(refreshTtl)
                .sameSite("Lax") // 🔥 IMPORTANT
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // ✅ Get Refresh Token
    public Optional<String> getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(refreshTokenCookieName))
                .map(c -> c.getValue())
                .findFirst();
    }

    // ✅ Delete Refresh Token
    public void deleteRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // (Optional) Access cookie if needed
    public void attachAccessCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie = ResponseCookie.from(accessTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}