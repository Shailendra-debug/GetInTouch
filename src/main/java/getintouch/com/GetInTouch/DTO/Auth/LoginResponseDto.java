package getintouch.com.GetInTouch.DTO.Auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class LoginResponseDto {

    private Long id;
    private String username;
    private String email;
    private String role;

    private String accessToken;
    private String refreshToken;

    // getters & setters
}