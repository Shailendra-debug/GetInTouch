package getintouch.com.GetInTouch.Service.User;
import getintouch.com.GetInTouch.DTO.Payment.PaymentResponseDto;
import getintouch.com.GetInTouch.DTO.Users.*;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;
import java.util.List;

public interface UserService {

    RegisterSendOtpResponseDto register(UserRegisterRequestDto request);

   // @PreAuthorize("hasRole('ADMIN')")
    UserResponseDto registerByAdmin(UserRegisterRequestDto request);

    //@PreAuthorize("hasRole('ADMIN')")
    List<UserResponseDto> getAll();

    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    UserResponseDto getById(Long id);

    //@PreAuthorize("hasAnyRole('ADMIN')")
    UserResponseDto getByGmail(String gmail);

    //@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    UserResponseDto update(Long id, UserUpdateRequestDto request);

    //@PreAuthorize("hasRole('ADMIN')")
    void delete(Long id);

//    @PreAuthorize("hasRole('ADMIN')")
    void makeAdmin(Long userId);

    //@PreAuthorize("hasRole('ADMIN')")
    void removeAdmin(Long userId);

    @Nullable UserResponseDto RegisterVerifyOtpSaveUser(@Valid RegisterVerifyOtpRequestDto request);


    UserResponseDto UpdateProfileImageUrl(String url,Long id);


}
