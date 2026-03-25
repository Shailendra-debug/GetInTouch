package getintouch.com.GetInTouch.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordResponseDto {

    private boolean passwordReset;
}