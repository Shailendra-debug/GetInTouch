package getintouch.com.GetInTouch.DTO.Payment;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private Long userId;
    private Long NotesId;
    private BigDecimal amount;
    private String currency; // "INR"
}
