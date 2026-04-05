package getintouch.com.GetInTouch.Service.Auth;

import getintouch.com.GetInTouch.DTO.Auth.*;
import getintouch.com.GetInTouch.Entity.User.PasswordResetOtp;
import getintouch.com.GetInTouch.Entity.User.RefreshToken;
import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Exception.BadRequestException;
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

    @Transactional
    public LoginResponseDto refreshToken(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        if (storedToken.isRevoked()) {
            throw new UnauthorizedException("Token already used (possible reuse attack)");
        }

        User user = storedToken.getUser();

        // 🔥 rotate (revoke old)
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // 🔥 generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        // 🔥 save new token (same device)
        saveRefreshToken(user, newRefreshToken);

        return LoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    /* ---------------- LOGOUT ---------------- */

    public void logout(String refreshToken) {

        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    /* ---------------- HELPERS ---------------- */

    private void saveRefreshToken(User user, String tokenValue) {

        RefreshToken token = new RefreshToken();

        token.setUser(user);
        token.setToken(tokenValue);
        token.setExpiryDate(jwtUtil.getRefreshExpiry());

        refreshTokenRepository.save(token);
    }

    public ForgotPasswordResponseDto sendOtp(ForgotPasswordRequestDto request) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        // avoid email enumeration
        if (userOpt.isEmpty()) {
            return ForgotPasswordResponseDto.builder()
                    .otpSent(true) // always true (security)
                    .build();
        }

        String email = request.getEmail();

//        otpRepo.findTopByEmailOrderByCreatedAtDesc(email).ifPresent(existing -> {
//            if (existing.getCreatedAt().plusMinutes(1).isAfter(LocalDateTime.now())) {
//                throw new BadRequestException("Too many requests. Try later.");
//            }
//        });

        String otp = otpUtil.generateOtp();

        PasswordResetOtp entity = PasswordResetOtp.builder()
                .email(email)
                .otpHash(passwordEncoder.encode(otp))
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        emailService.sendOtp(email, otp);
        otpRepo.save(entity);


        return ForgotPasswordResponseDto.builder()
                .otpSent(true)
                .build();
    }

    // ✅ RESET PASSWORD
    @Transactional
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto request) {

        PasswordResetOtp otpEntity = otpRepo
                .findTopByEmailOrderByCreatedAtDesc(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid request"));

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        if (otpEntity.getAttemptCount() >= MAX_ATTEMPTS) {
            throw new BadRequestException("Too many failed attempts");
        }

        if (!passwordEncoder.matches(request.getOtp(), otpEntity.getOtpHash())) {

            otpEntity.setAttemptCount(otpEntity.getAttemptCount() + 1);
            otpRepo.save(otpEntity);

            throw new BadRequestException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpRepo.deleteByEmail(request.getEmail());

        return ResetPasswordResponseDto.builder()
                .passwordReset(true)
                .build();
    }

}
