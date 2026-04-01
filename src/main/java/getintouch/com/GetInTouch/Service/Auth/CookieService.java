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

    @Value("${security.jwt.refresh-token-cookie-name}")
    private String refreshTokenCookieName;

    @Value("${security.jwt.cookie-secure}")
    private boolean cookieSecure;

    @Value("${security.jwt.cookie-http-only}")
    private boolean cookieHttpOnly;

    @Value("${security.jwt.cookie-same-site}")
    private String cookieSameSite;

    @Value("${security.jwt.refresh-ttl-seconds}")
    private long refreshTtl;

    @Value("${security.jwt.cookie-domain:}")
    private String cookieDomain; // optional

    // ✅ Attach Refresh Token Cookie
    public void attachRefreshCookie(HttpServletResponse response, String value) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshTtl)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    // ✅ Get Cookie Value
    public Optional<String> getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(refreshTokenCookieName))
                .map(c -> c.getValue())
                .findFirst();
    }

    // ✅ Delete Cookie
    public void deleteRefreshCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(refreshTokenCookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }
}