package getintouch.com.GetInTouch.Service.User;

import getintouch.com.GetInTouch.Configuration.PasswordConfig;
import getintouch.com.GetInTouch.DTO.Auth.ResetPasswordRequestDto;
import getintouch.com.GetInTouch.DTO.Auth.ResetPasswordResponseDto;
import getintouch.com.GetInTouch.DTO.Users.*;
import getintouch.com.GetInTouch.Entity.User.PasswordResetOtp;
import getintouch.com.GetInTouch.Entity.User.RegisterUser;
import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.UserMapper;
import getintouch.com.GetInTouch.Repository.PasswordResetOtpRepository;
import getintouch.com.GetInTouch.Repository.RegisterUserRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import getintouch.com.GetInTouch.Service.Auth.EmailService;
import getintouch.com.GetInTouch.Util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final int MAX_ATTEMPTS = 5;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetOtpRepository otpRepo;
    private final OtpUtil otpUtil;
    private final EmailService emailService;
    private final RegisterUserRepository registerUserRepository;

    /* ---------- CREATE ---------- */

    @Override
    public RegisterSendOtpResponseDto register(UserRegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        Optional<RegisterUser> registerUser=registerUserRepository.findByEmail(request.getEmail());
        registerUser.ifPresent(registerUserRepository::delete);

        RegisterUser user=new RegisterUser();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        boolean b=sendRegisterOtp(user.getEmail());
        if (b) {
            registerUserRepository.save(user);
            return new RegisterSendOtpResponseDto(true);
        }
        return new RegisterSendOtpResponseDto(false);
    }

    /* ---------- READ ---------- */

    @Override
    public UserResponseDto getById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    /* ---------- UPDATE ---------- */

    @Override
    public UserResponseDto update(Long id, UserUpdateRequestDto request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        return UserMapper.toDto(userRepository.save(user));
    }

    /* ---------- DELETE ---------- */

    @Override
    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void makeAdmin(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }
    public boolean sendRegisterOtp(String email) {

        // ✅ Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }

//        // ✅ Rate limiting (1 min)
//        otpRepo.findTopByEmailOrderByCreatedAtDesc(email).ifPresent(existing -> {
//            if (existing.getCreatedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
//                throw new BadRequestException("OTP already Send. Try After 60 Second.");
//            }
//        });

        String otp = otpUtil.generateOtp();

        PasswordResetOtp entity = PasswordResetOtp.builder()
                .email(email)
                .otpHash(passwordEncoder.encode(otp)) // ✅ hashed OTP
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        emailService.sendRegisterOtp(email, otp);

        otpRepo.save(entity);



        return true;

    }
    // ✅ RESET PASSWORD
    @jakarta.transaction.Transactional
    @Transactional
    public UserResponseDto RegisterVerifyOtpSaveUser(RegisterVerifyOtpRequestDto request) {

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

        // ✅ delete OTP after success
        otpRepo.deleteByEmail(request.getEmail());

        return saveUser(request);
    }
    private UserResponseDto saveUser(RegisterVerifyOtpRequestDto request) {

        RegisterUser user = registerUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        UserRegisterRequestDto dto = new UserRegisterRequestDto();

        dto.setEmail(user.getEmail());
        dto.setRole(Role.USER);
        dto.setPhone(user.getPhone());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());

        // ✅ password should already be encoded earlier

        User newUser = UserMapper.toEntity(dto);
        newUser.setPassword(user.getPassword());
        newUser.setEnabled(true);
        newUser.setAccountLocked(false);
        newUser.setRole(Role.USER);
        // ✅ save first
        User savedUser = userRepository.save(newUser);
        // ✅ delete temp after success
        registerUserRepository.delete(user);

        return UserMapper.toDto(savedUser);
    }
}
