package getintouch.com.GetInTouch.Service.Payments;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import getintouch.com.GetInTouch.DTO.Payment.PaymentInitiateResponseDTO;
import getintouch.com.GetInTouch.DTO.Payment.PaymentRequestDTO;
import getintouch.com.GetInTouch.DTO.Payment.PaymentResponseDto;
import getintouch.com.GetInTouch.DTO.Payment.PaymentVerifyRequestDTO;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Entity.Note.Purchase;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import getintouch.com.GetInTouch.Entity.Razorpay.Payment;
import getintouch.com.GetInTouch.Entity.Razorpay.PaymentStatus;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.*;
import getintouch.com.GetInTouch.Service.Auth.EmailService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final PurchaseRepository purchaseRepository;
    private final NotesRepository notesRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.webhook-secret}") // Separate secret configured in Razorpay Dashboard
    private String webhookSecret;

    @Transactional
    public PaymentInitiateResponseDTO initiatePayment(PaymentRequestDTO request) throws Exception {
        int amountInPaise = request.getAmount().multiply(new BigDecimal("100")).intValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", request.getCurrency());
        orderRequest.put("receipt", "rcpt_" + UUID.randomUUID().toString().substring(0, 8));

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id");

        User userProxy = userRepository.getReferenceById(request.getUserId());
        Notes noteProxy = notesRepository.getReferenceById(request.getNotesId());

        Payment payment = Payment.builder()
                .razorpayOrderId(razorpayOrderId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.PENDING)
                .user(userProxy)
                .note(noteProxy)
                .build();

        paymentRepository.save(payment);

        return new PaymentInitiateResponseDTO(razorpayOrderId, amountInPaise, request.getCurrency());
    }

    @Transactional
    public void verifyPayment(PaymentVerifyRequestDTO request) {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", request.getRazorpayOrderId());
        options.put("razorpay_payment_id", request.getRazorpayPaymentId());
        options.put("razorpay_signature", request.getRazorpaySignature());

        // 1. Lock the row immediately to prevent concurrent webhook conflicts
        Payment payment = paymentRepository.findByRazorpayOrderIdForUpdate(request.getRazorpayOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction Order matching reference not found"));

        try {
            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);
            if (!isValid) {
                handlePaymentFailure(payment, request.getRazorpayPaymentId(), "FAILED (Invalid Signature)");
                throw new SecurityException("Fraud Alert: Invalid signature payload override attempted.");
            }

            fulfillPayment(payment, request.getRazorpayPaymentId());

        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            handlePaymentFailure(payment, request.getRazorpayPaymentId(), "FAILED");
            throw new RuntimeException("Secure payment signature validation routine failed.", e);
        }
    }

    @Transactional
    public void processWebhookPayload(String rawPayload, String signatureHeader) {
        try {
            // 1. CRITICAL: Verify webhook signature before processing
            boolean isValidWebhook = Utils.verifyWebhookSignature(rawPayload, signatureHeader, webhookSecret);
            if (!isValidWebhook) {
                throw new SecurityException("Invalid webhook signature threat detected.");
            }

            JSONObject jsonEvent = new JSONObject(rawPayload);
            String eventType = jsonEvent.getString("event");

            JSONObject paymentEntity = jsonEvent.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            String razorpayPaymentId = paymentEntity.getString("id");
            String razorpayOrderId = paymentEntity.getString("order_id");

            // 2. Lock the row to isolate processing logic safely
            paymentRepository.findByRazorpayOrderIdForUpdate(razorpayOrderId).ifPresent(payment -> {
                if ("order.paid".equals(eventType) || "payment.captured".equals(eventType)) {
                    fulfillPayment(payment, razorpayPaymentId);
                } else if ("payment.failed".equals(eventType)) {
                    handlePaymentFailure(payment, razorpayPaymentId, "FAILED");
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Webhook orchestration failure", e);
        }
    }

    // Isolated Fulfillment logic to prevent boilerplate copy-pasting
    private void fulfillPayment(Payment payment, String razorpayPaymentId) {
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            User buyer = payment.getUser();
            Notes purchasedNote = payment.getNote();

            Purchase userAccessRecord = new Purchase();
            userAccessRecord.setUser(buyer);
            userAccessRecord.setNote(purchasedNote);
            userAccessRecord.setPurchaseDate(LocalDateTime.now());
            userAccessRecord.setAmountPaid(payment.getAmount());
            userAccessRecord.setPaymentStatus("SUCCESS");
            purchaseRepository.save(userAccessRecord);

            emailService.sendPaymentStatusEmail(
                    buyer.getEmail(), buyer.getFullName(), payment.getRazorpayOrderId(),
                    razorpayPaymentId, String.valueOf(payment.getAmount()), "SUCCESS"
            );
        }
    }

    private void handlePaymentFailure(Payment payment, String paymentId, String statusString) {
        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setRazorpayPaymentId(paymentId);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            User buyer = payment.getUser();
            emailService.sendPaymentStatusEmail(
                    buyer.getEmail(), buyer.getFullName(), payment.getRazorpayOrderId(),
                    paymentId, String.valueOf(payment.getAmount()), statusString
            );
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPaymentsByUser(Long usrId) {
        // Uses the JOIN FETCH query version to avoid N+1 issues
        return paymentRepository.findByUserIdWithRelationships(usrId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getAllPaymentsById(Long id) {
        // Fetching directly via findById to ensure parameters map smoothly out of proxies
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found"));
        return toResponseDto(payment);
    }

    public PaymentResponseDto toResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .userId(payment.getUser().getId())
                .userName(payment.getUser().getFullName())
                .noteId(payment.getNote().getId())
                .noteTitle(payment.getNote().getTitle())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}