package getintouch.com.GetInTouch.DTO.Auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
}
