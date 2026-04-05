package getintouch.com.GetInTouch.DTO.Users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterSendOtpResponseDto {


    @Schema(example = "true")
    private boolean otpSent;

}
