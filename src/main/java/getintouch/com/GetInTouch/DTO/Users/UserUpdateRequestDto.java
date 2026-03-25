package getintouch.com.GetInTouch.DTO.Users;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRequestDto {

    private String username;

    @Email
    private String email;

    private String fullName;
    private String phone;
    private Boolean enabled;
}
