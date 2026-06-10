package getintouch.com.GetInTouch.DTO.Payment;

import getintouch.com.GetInTouch.Entity.Razorpay.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDto {

    private Long id;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private BigDecimal amount;

    private String currency;

    private PaymentStatus status;

    private Long userId;

    private String userName;

    private Long noteId;

    private String noteTitle;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
