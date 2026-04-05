package getintouch.com.GetInTouch.Service.User;
import getintouch.com.GetInTouch.DTO.Users.*;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService {

    RegisterSendOtpResponseDto register(UserRegisterRequestDto request);

    @PreAuthorize("hasRole('ADMIN')")
    List<UserResponseDto> getAll();

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    UserResponseDto getById(Long id);

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    UserResponseDto update(Long id, UserUpdateRequestDto request);

    @PreAuthorize("hasRole('ADMIN')")
    void delete(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    void makeAdmin(Long userId);

    @Nullable UserResponseDto RegisterVerifyOtpSaveUser(@Valid RegisterVerifyOtpRequestDto request);
}
