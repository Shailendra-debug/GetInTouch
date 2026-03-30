package getintouch.com.GetInTouch.DTO.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request DTO for sending OTP")
@Getter @Setter
public class ForgotPasswordRequestDto {

    @Schema(example = "user@gmail.com", description = "User email")
    @NotBlank
    @Email
    private String email;
}
