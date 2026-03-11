package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Auth.LoginRequestDTO;
import getintouch.com.GetInTouch.DTO.Auth.LoginResponseDto;
import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.Service.Auth.AuthService;
import getintouch.com.GetInTouch.Service.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        return ResponseEntity.ok(authService.login(request, response));
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
       LoginResponseDto dto= authService.refreshToken(request, response);
       return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public Authentication me(Authentication authentication) {
        return authentication;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        authService.logout(request, response);
        return ResponseEntity.ok("Logged out successfully");
    }
}
