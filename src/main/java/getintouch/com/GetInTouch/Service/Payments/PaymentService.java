package getintouch.com.GetInTouch.Service.Payments;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import getintouch.com.GetInTouch.DTO.Payment.PaymentInitiateResponseDTO;
import getintouch.com.GetInTouch.DTO.Payment.PaymentRequestDTO;
import getintouch.com.GetInTouch.DTO.Payment.PaymentVerifyRequestDTO;
import getintouch.com.GetInTouch.Entity.Razorpay.Payment;
import getintouch.com.GetInTouch.Entity.Razorpay.PaymentStatus;
import getintouch.com.GetInTouch.Repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

//    private final RazorpayClient razorpayClient;
//    private final PaymentRepository paymentRepository;
//
//    @Value("${razorpay.key-secret}")
//    private String keySecret;
//
//    @Transactional
//    public PaymentInitiateResponseDTO initiatePayment(PaymentRequestDTO request) throws Exception {
//        // Convert Rupees to Paise (multiply by 100)
//        int amountInPaise = request.getAmount().multiply(new BigDecimal("100")).intValue();
//
//        // 1. Tell Razorpay to create an order
//        JSONObject orderRequest = new JSONObject();
//        orderRequest.put("amount", amountInPaise);
//        orderRequest.put("currency", request.getCurrency());
//        orderRequest.put("receipt", "rcpt_" + UUID.randomUUID().toString().substring(0, 8));
//
//        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
//        String razorpayOrderId = razorpayOrder.get("id");
//
//        // 2. Save payment record in DB as PENDING
//        Payment payment = Payment.builder()
//                .razorpayOrderId(razorpayOrderId)
//                .amount(request.getAmount())
//                .currency(request.getCurrency())
//                .status(PaymentStatus.PENDING)
//                .userId(request.getUserId())
//                .courseId(request.getNotesId())
//                .build();
//
//        paymentRepository.save(payment);
//
//        return new PaymentInitiateResponseDTO(razorpayOrderId, amountInPaise, request.getCurrency());
//    }

//    @Transactional
//    public void verifyPayment(PaymentVerifyRequestDTO request) {
//        // 1. Recreate the signature data packet format
//        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
//
//        try {
//            // 2. Mathematically check if the signature matches our secret key
//            boolean isValid = Utils.verifyPaymentSignature(payload, request.getRazorpaySignature(), keySecret);
//
//            if (!isValid) {
//                throw new SecurityException("Signature verification failed! Data was manipulated.");
//            }
//
//            // 3. Find our database record and update it to SUCCESS
//            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
//                    .orElseThrow(() -> new IllegalArgumentException("Order ID not found"));
//
//            if (payment.getStatus() != PaymentStatus.SUCCESS) {
//                payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
//                payment.setRazorpaySignature(request.getRazorpaySignature());
//                payment.setStatus(PaymentStatus.SUCCESS);
//                paymentRepository.save(payment);
//
//                // TODO: Unlock course/Send email here
//                System.out.println("Payment Successful! Course unlocked for user: " + payment.getUserId());
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("Payment processing failed securely", e);
//        }
//    }
}