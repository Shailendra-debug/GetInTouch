package getintouch.com.GetInTouch.Service.User;

import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.DTO.Users.UserUpdateRequestDto;
import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.UserMapper;
import getintouch.com.GetInTouch.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* ---------- CREATE ---------- */

    @Override
    public UserResponseDto register(UserRegisterRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setRole(Role.USER);

        return UserMapper.toDto(userRepository.save(user));
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
}
