package getintouch.com.GetInTouch.DTO.Payment;

import lombok.Data;

@Data
public class PaymentVerifyRequestDTO {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
