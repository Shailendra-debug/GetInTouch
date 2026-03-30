package getintouch.com.GetInTouch.Filter;

import getintouch.com.GetInTouch.Util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServletServerHttpRequest;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {

            HttpServletRequest req = servletRequest.getServletRequest();

            if (req.getCookies() != null) {
                for (Cookie cookie : req.getCookies()) {

                    if ("ACCESS_TOKEN".equals(cookie.getName())) {

                        String token = cookie.getValue();

                        if (jwtUtil.isTokenValid(token)) {

                            Long userId = jwtUtil.extractUserId(token);

                            // 🔥 store userId for later use
                            attributes.put("userId", userId);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}