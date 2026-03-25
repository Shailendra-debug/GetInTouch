package getintouch.com.GetInTouch.security;

import getintouch.com.GetInTouch.DTO.Auth.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex) throws IOException {

        ApiError error = new ApiError();
        error.setStatus(403);
        error.setError("FORBIDDEN");
        error.setMessage("Access Denied");
        error.setTimestamp(LocalDateTime.now());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        response.getWriter()
                .write(new ObjectMapper().writeValueAsString(error));
    }
}