package getintouch.com.GetInTouch.DTO.Users;

import lombok.Data;

@Data
public class RegisterVerifyOtpRequestDto {

    private String email;
    private String otp;
}
