package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Auth.*;
import getintouch.com.GetInTouch.DTO.Users.RegisterSendOtpResponseDto;
import getintouch.com.GetInTouch.DTO.Users.RegisterVerifyOtpRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.Exception.UnauthorizedException;
import getintouch.com.GetInTouch.Service.Auth.AuthService;
import getintouch.com.GetInTouch.Service.Auth.CookieService;
import getintouch.com.GetInTouch.Service.User.UserService;
import getintouch.com.GetInTouch.Util.ResponseUtil;
import getintouch.com.GetInTouch.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final CookieService cookieService;

    @Operation(summary = "User Login", description = "Authenticate user and return JWT tokens")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response,
            HttpServletRequest httpRequest) {

        LoginResponseDto dto = authService.login(request);

        boolean isWeb = httpRequest.getHeader("X-Client-Type") == null;

        if (dto.getRefreshToken() != null && isWeb) {
            //cookieService.attachAccessCookie(response, dto.getAccessToken());
            cookieService.attachRefreshCookie(response, dto.getRefreshToken());
            dto.setRefreshToken(dto.getRefreshToken()); // 🔥 hide for web
        }

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "User Registration", description = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping("/register")
    public ResponseEntity<RegisterSendOtpResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @Operation(
            summary = "Verify OTP and Complete Registration",
            description = "Verify OTP sent to email and create user account"
    )
    @ApiResponse(responseCode = "200", description = "OTP verified and user registered successfully")
    @PostMapping("/verify-otp")
    public ResponseEntity<UserResponseDto> verifyOtp(
            @Valid @RequestBody RegisterVerifyOtpRequestDto request) {

        return ResponseEntity.ok(userService.RegisterVerifyOtpSaveUser(request));
    }

    @Operation(summary = "Refresh Token", description = "Generate new access token using refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) RefreshTokesDto body
    ) {

        String refreshToken = cookieService.getRefreshToken(request)
                .orElse(body != null ? body.getRefreshToken() : null);

        if (refreshToken == null) {
            throw new UnauthorizedException("Refresh token not found");
        }

        LoginResponseDto dto = authService.refreshToken(refreshToken);

        boolean isWeb = cookieService.getRefreshToken(request).isPresent();

        if (isWeb) {
            //cookieService.attachAccessCookie(response, dto.getAccessToken());
            cookieService.attachRefreshCookie(response, dto.getRefreshToken());
            dto.setRefreshToken(dto.getRefreshToken());
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.getById(userId));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) RefreshTokesDto body
    ) {

        String refreshToken = null;

        // ✅ 1. Try cookie (Web)
        refreshToken = cookieService.getRefreshToken(request).orElse(null);

        // ✅ 2. Fallback to body (Android)
        if (refreshToken == null && body != null) {
            refreshToken = body.getRefreshToken();
        }

        // ✅ 3. Invalidate refresh token in DB
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        // 🍪 4. ALWAYS delete cookies
        cookieService.deleteRefreshCookie(response);
        //cookieService.deleteAccessCookie(response);

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
