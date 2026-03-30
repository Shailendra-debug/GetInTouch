package getintouch.com.GetInTouch.DTO.Auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class ApiError {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

}