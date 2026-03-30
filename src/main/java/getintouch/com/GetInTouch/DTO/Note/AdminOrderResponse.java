package getintouch.com.GetInTouch.DTO.Note;

import lombok.*;

@Getter
@Setter
@Builder
public class AdminOrderResponse {

    private Long orderId;
    private Long userId;
    private Long noteId;
    private String screenshotUrl;
    private String status;
    private String createdAt;
}