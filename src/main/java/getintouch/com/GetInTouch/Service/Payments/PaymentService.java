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
        // 1. Prepare JSON object for Razorpay SDK verification
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", request.getRazorpayOrderId());
        options.put("razorpay_payment_id", request.getRazorpayPaymentId());
        options.put("razorpay_signature", request.getRazorpaySignature());

        try {
            // 2. Perform signature verification
            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

            if (!isValid) {
                throw new SecurityException("Fraud Alert: Invalid signature payload override attempted.");
            }

            // 3. Fetch the pending transaction record
            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction Order matching reference not found"));

            // 4. Idempotency Check: Only update if it hasn't been handled by a Webhook already
            if (payment.getStatus() != PaymentStatus.SUCCESS) {

                // Update payment tracking data
                payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
                payment.setRazorpaySignature(request.getRazorpaySignature());
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);

                // 5. Grant access directly to the purchased Note
                User buyer = payment.getUser();
                Notes purchasedNote = payment.getNote(); // Extracted directly from the updated payment mapping

                // Create a single purchase record for this specific note
                Purchase userAccessRecord = new Purchase();
                userAccessRecord.setUser(buyer);
                userAccessRecord.setNote(purchasedNote);
                userAccessRecord.setPurchaseDate(LocalDateTime.now());
                userAccessRecord.setAmountPaid(payment.getAmount());
                userAccessRecord.setPaymentStatus(PaymentStatus.SUCCESS.toString());

                purchaseRepository.save(userAccessRecord);

                System.out.println("Success! Note '" + purchasedNote.getTitle() + "' unlocked for user: " + buyer.getEmail());
            }

        } catch (Exception e) {
            throw new RuntimeException("Secure payment signature validation routine failed aborted execution.", e);
        }
    }

    @Transactional
    public void processWebhookPayload(String rawPayload) {
        // 1. Parse the incoming string into a JSON Object
        JSONObject jsonEvent = new JSONObject(rawPayload);
        String eventType = jsonEvent.getString("event");

        // 2. We only care about successful payment completions
        if ("order.paid".equals(eventType)) {

            // Extract the order sub-entity from the complex payload structure
            JSONObject orderEntity = jsonEvent.getJSONObject("payload")
                    .getJSONObject("order")
                    .getJSONObject("entity");

            String razorpayOrderId = orderEntity.getString("id");

            // Extract the payment ID as well for our audit records
            // The paid event contains an array of payments; grab the first one
            String razorpayPaymentId = jsonEvent.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity")
                    .getString("id");

            // 3. Find the pending transaction row
            paymentRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(payment -> {

                // Idempotency: Ensure we don't double-process if verification API already ran
                if (payment.getStatus() != PaymentStatus.SUCCESS) {

                    // Update payment status
                    payment.setRazorpayPaymentId(razorpayPaymentId);
                    payment.setStatus(PaymentStatus.SUCCESS);
                    paymentRepository.save(payment);

                    // Grant access to the Note
                    User buyer = payment.getUser();
                    Notes purchasedNote = payment.getNote();

                    Purchase userAccessRecord = new Purchase();
                    userAccessRecord.setUser(buyer);
                    userAccessRecord.setNote(purchasedNote);
                    userAccessRecord.setPurchaseDate(LocalDateTime.now());
                    userAccessRecord.setAmountPaid(payment.getAmount());
                    userAccessRecord.setPaymentStatus("SUCCESS");

                    purchaseRepository.save(userAccessRecord);

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