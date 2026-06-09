package getintouch.com.GetInTouch.DTO.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentInitiateResponseDTO {
    private String razorpayOrderId;
    private int amount; // in paise
    private String currency;
}
