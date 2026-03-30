package getintouch.com.GetInTouch.DTO.Users;

import getintouch.com.GetInTouch.Entity.User.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class UserRegisterRequestDto {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    private String fullName;
    private String phone;

    private Role role;



    // getters & setters
}
