package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Auth.*;
import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.Service.Auth.AuthService;
import getintouch.com.GetInTouch.Service.User.UserService;
import getintouch.com.GetInTouch.Util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication APIs", description = "Login, Register, JWT Refresh, Logout")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "User Login", description = "Authenticate user and return JWT tokens")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        return ResponseEntity.ok(authService.login(request, response));
    }

    @Operation(summary = "User Registration", description = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    @Operation(summary = "Refresh Token", description = "Generate new access token using refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        LoginResponseDto dto = authService.refreshToken(request, response);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get Current User", description = "Returns authenticated user details")
    @GetMapping("/me")
    public Authentication me(Authentication authentication) {
        return authentication;
    }

    @Operation(summary = "Logout", description = "Invalidate user session and tokens")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        authService.logout(request, response);
        return ResponseEntity.ok("Logged out successfully");
    }

    @Operation(
            summary = "Send OTP for Forgot Password",
            description = "Generates and sends OTP to user's email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO<ForgotPasswordResponseDto>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto request) {

        ForgotPasswordResponseDto response = authService.sendOtp(request);

        if (response.isOtpSent())
            return ResponseEntity.ok(
                ResponseUtil.success("If email exists, OTP sent", response)
        );
        return ResponseEntity.ok(
                ResponseUtil.success("Email Not Exists, OTP Not sent", response));
    }

    @Operation(
            summary = "Reset Password using OTP",
            description = "Verifies OTP and resets user password"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP / Expired OTP")
    })

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<ResetPasswordResponseDto>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto request) {

        ResetPasswordResponseDto response = authService.resetPassword(request);

        return ResponseEntity.ok(
                ResponseUtil.success("Password reset successful", response)
        );
    }
}
