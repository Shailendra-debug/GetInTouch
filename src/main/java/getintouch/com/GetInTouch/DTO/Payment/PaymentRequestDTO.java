package getintouch.com.GetInTouch.DTO.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long userId;
    private Long NotesId;
    private BigDecimal amount;
    private String currency; // "INR"
}
