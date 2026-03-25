package getintouch.com.GetInTouch.DTO.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "Reset password response")
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ResetPasswordResponseDto {

    @Schema(example = "true")
    private boolean passwordReset;
}