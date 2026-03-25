package getintouch.com.GetInTouch.Service.User;
import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.DTO.Users.UserUpdateRequestDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService {

    UserResponseDto register(UserRegisterRequestDto request);

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

}
