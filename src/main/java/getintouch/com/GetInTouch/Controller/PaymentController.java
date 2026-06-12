package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Payment.PaymentResponseDto;
import getintouch.com.GetInTouch.DTO.Payment.PaymentVerifyRequestDTO;
import getintouch.com.GetInTouch.Service.Payments.PaymentService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /* =====================================================
       STEP 3: VERIFY PAYMENT (Frontend Success Callback)
       ===================================================== */
    @Operation(
            summary = "Verify Razorpay Payment",
            description = "Verifies Razorpay payment signature after successful payment."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @Valid @RequestBody PaymentVerifyRequestDTO request) {

        paymentService.verifyPayment(request);
        return ResponseEntity.ok("Payment processed and verified successfully!");
    }

    /* =====================================================
       BACKUP SAFETY NET: RAZORPAY WEBHOOK
       ===================================================== */
    @Operation(
            summary = "Razorpay Webhook",
            description = "Receives payment events directly from Razorpay."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook processed"),
            @ApiResponse(responseCode = "400", description = "Invalid signature"),
            @ApiResponse(responseCode = "500", description = "Processing failed")
    })
    @PostMapping("/webhook")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String signaturePayload,
            @RequestHeader("X-Razorpay-Signature") String razorpaySignature) {

        try {
            // Verification logic has been delegated fully to the transactional service layer
            // to ensure consistency and prevent signature-override bypasses.
            paymentService.processWebhookPayload(signaturePayload, razorpaySignature);

            // Always return 200 OK immediately to stop Razorpay from retrying the hook
            return ResponseEntity.ok("Webhook Received");

        } catch (SecurityException e) {
            log.warn("Unauthorized webhook payload threat detected: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
        } catch (Exception e) {
            log.error("Error occurred while processing Razorpay webhook event data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing Failed");
        }
    }

    @Operation(
            summary = "Get My Payments",
            description = "Returns all payments made by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments() {
        Long currentUser = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(
                paymentService.getAllPaymentsByUser(currentUser)
        );
    }

    @Operation(
            summary = "Get Payment By ID",
            description = "Returns a payment using its database ID if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not your payment record"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/my-payments/{id}")
    public ResponseEntity<?> getPaymentById(
            @Parameter(description = "Payment ID", example = "1")
            @PathVariable Long id) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        PaymentResponseDto payment = paymentService.getAllPaymentsById(id);

        // Security Guardrail: Prevent cross-user data scraping
        if (!payment.getUserId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You do not have permission to view this transaction.");
        }

        return ResponseEntity.ok(payment);
    }
}