package getintouch.com.GetInTouch.DTO.Note;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long noteId;
    private String status;
    private String paymentQrUrl;
}