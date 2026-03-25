package getintouch.com.GetInTouch.DTO.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private LocalDateTime createdAt;

    // getters & setters
}