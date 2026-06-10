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

    @Transactional
    public PaymentInitiateResponseDTO initiatePayment(PaymentRequestDTO request) throws Exception {
        // 1. Convert Rupees to Paise (multiply by 100)
        int amountInPaise = request.getAmount().multiply(new BigDecimal("100")).intValue();

        // 2. Tell Razorpay to create an order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", request.getCurrency());
        orderRequest.put("receipt", "rcpt_" + UUID.randomUUID().toString().substring(0, 8));

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id");

        // 3. Get lightweight proxy references for our relationships
        User userProxy = userRepository.getReferenceById(request.getUserId());
        Notes noteProxy = notesRepository.getReferenceById(request.getNotesId()); // Fetch Notes proxy reference

        // 4. Save payment record in DB as PENDING
        Payment payment = Payment.builder()
                .razorpayOrderId(razorpayOrderId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.PENDING)
                .user(userProxy) // Set the user mapping
                .note(noteProxy) // Set the note mapping directly
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

        // 1. Fetch the payment record first so we can update it if something goes wrong
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction Order matching reference not found"));

        try {
            // 2. Perform signature verification
            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

            if (!isValid) {
                // Handle signature mismatch / potential tampering
                handlePaymentFailure(payment, request.getRazorpayPaymentId(), "FAILED (Invalid Signature)");
                throw new SecurityException("Fraud Alert: Invalid signature payload override attempted.");
            }

            // 3. Process Success (Only if it hasn't been handled yet)
            if (payment.getStatus() != PaymentStatus.SUCCESS) {
                payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
                payment.setRazorpaySignature(request.getRazorpaySignature());
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);

                User buyer = payment.getUser();
                Notes purchasedNote = payment.getNote();

                Purchase userAccessRecord = new Purchase();
                userAccessRecord.setUser(buyer);
                userAccessRecord.setNote(purchasedNote);
                userAccessRecord.setPurchaseDate(LocalDateTime.now());
                userAccessRecord.setAmountPaid(payment.getAmount());
                userAccessRecord.setPaymentStatus(PaymentStatus.SUCCESS.toString());
                purchaseRepository.save(userAccessRecord);

                // Send Success Email
                emailService.sendPaymentStatusEmail(
                        buyer.getEmail(), buyer.getFullName(), payment.getRazorpayOrderId(),
                        payment.getRazorpayPaymentId(), String.valueOf(payment.getAmount()), "SUCCESS"
                );
            }

        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            // Fallback for any unexpected system runtime issues during verification
            handlePaymentFailure(payment, request.getRazorpayPaymentId(), "FAILED");
            throw new RuntimeException("Secure payment signature validation routine failed.", e);
        }
    }

    // Private helper to clean up duplicate code for handling failures
    private void handlePaymentFailure(Payment payment, String paymentId, String statusString) {
        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setRazorpayPaymentId(paymentId);
            payment.setStatus(PaymentStatus.FAILED); // Make sure PaymentStatus.FAILED enum exists
            paymentRepository.save(payment);

            User buyer = payment.getUser();
            emailService.sendPaymentStatusEmail(
                    buyer.getEmail(), buyer.getFullName(), payment.getRazorpayOrderId(),
                    paymentId, String.valueOf(payment.getAmount()), statusString
            );
        }
    }
    @Transactional
    public void processWebhookPayload(String rawPayload) {
        JSONObject jsonEvent = new JSONObject(rawPayload);
        String eventType = jsonEvent.getString("event");

        // Extract common fields safely
        JSONObject paymentEntity = jsonEvent.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayPaymentId = paymentEntity.getString("id");
        String razorpayOrderId = paymentEntity.getString("order_id");

        // CASE A: Payment was successful
        if ("order.paid".equals(eventType) || "payment.captured".equals(eventType)) {
            paymentRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(payment -> {
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
            });
        }

        // CASE B: Payment explicitly failed on checkout
        else if ("payment.failed".equals(eventType)) {
            paymentRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(payment -> {
                // Only update if it hasn't somehow already marked successful
                if (payment.getStatus() == PaymentStatus.PENDING) {
                    payment.setRazorpayPaymentId(razorpayPaymentId);
                    payment.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);

                    User buyer = payment.getUser();
                    // Triggers template with red styling and ❌ subject automatically
                    emailService.sendPaymentStatusEmail(
                            buyer.getEmail(), buyer.getFullName(), payment.getRazorpayOrderId(),
                            razorpayPaymentId, String.valueOf(payment.getAmount()), "FAILED"
                    );
                }
            });
        }
    }

    public List<PaymentResponseDto> getAllPaymentsByUser(Long usrId) {

        return paymentRepository.findByUserId(usrId).stream().map(this::toResponseDto).toList();
    }


    public PaymentResponseDto getAllPaymentsById(Long id) {
        return toResponseDto(paymentRepository.getReferenceById(id));
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