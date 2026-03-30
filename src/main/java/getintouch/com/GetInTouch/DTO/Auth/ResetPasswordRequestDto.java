package getintouch.com.GetInTouch.DTO.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request DTO for resetting password")
@Getter @Setter
public class ResetPasswordRequestDto {

    @Schema(example = "user@gmail.com")
    @NotBlank @Email
    private String email;

    @Schema(example = "123456")
    @NotBlank
    private String otp;

    @Schema(example = "newPass123")
    @NotBlank
    @Size(min = 6)
    private String newPassword;
}