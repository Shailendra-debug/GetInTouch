package getintouch.com.GetInTouch.Service.Auth;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {
    
    @Value("${resend.api-key}")
    private String apiKey;

    private static final String FROM_EMAIL = "support@skushwaha.in";

    public void sendRegisterOtp(String to, String otp) {
        sendEmail(to, "Verify Your Email", buildRegisterTemplate(otp));
    }

    public void sendResetOtp(String to, String otp) {
        sendEmail(to, "Reset Your Password", buildResetTemplate(otp));
    }

    /**
     * Sends a stylized payment status email to the user.
     */
    public void sendPaymentStatusEmail(
            String to,
            String userName,
            String orderId,
            String paymentId,
            String amount,
            String status) {

        // Dynamically tailor the subject line based on the transaction result
        String subject = switch (status.toUpperCase()) {
            case "SUCCESS" -> "🎉 Payment Successful - GetInTouch";
            case "FAILED" -> "❌ Payment Failed - GetInTouch";
            case "ADMIN_GRANTED" -> "🎉 Pay By ADMIN - GetInTouch";
            default -> "⏳ Payment Update - GetInTouch";
        };

        String htmlContent = buildPaymentStatusTemplate(userName, orderId, paymentId, amount, status);
        sendEmail(to, subject, htmlContent);
    }

    // 🔥 Common method
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            Resend resend = new Resend(apiKey);

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(FROM_EMAIL)
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            resend.emails().send(params);

        } catch (Exception e) {
            // log internally (add logger if needed)
            throw new BadRequestException("Unable to send email. Please try again.");
        }
    }

    // 🔥 Register Template
    private String buildRegisterTemplate(String otp) {
        return """
        <html>
        <body style="font-family: Arial; background:#f4f4f4; padding:20px;">
            <div style="max-width:500px;margin:auto;background:#fff;padding:20px;border-radius:10px;text-align:center;">
                
                <h2>🎉 Welcome to GetInTouch</h2>
                <p>Thanks for registering!</p>

                <p>Your OTP:</p>
                <div style="font-size:30px;font-weight:bold;color:#27ae60;margin:20px;">%s</div>

                <p>Valid for 5 minutes</p>

                <div style="font-size:12px;color:#888;margin-top:20px;">
                    © 2026 GetInTouch
                </div>
            </div>
        </body>
        </html>
        """.formatted(otp);
    }

    // 🔥 Reset Template
    private String buildResetTemplate(String otp) {
        return """
        <html>
        <body style="font-family: Arial; background:#f4f4f4; padding:20px;">
            <div style="max-width:500px;margin:auto;background:#fff;padding:20px;border-radius:10px;text-align:center;">
                
                <h2>🔐 Password Reset</h2>
                <p>We received a request to reset your password.</p>

                <p>Your OTP:</p>
                <div style="font-size:28px;font-weight:bold;color:#2c3e50;margin:20px;">%s</div>

                <p>Valid for 5 minutes</p>
            </div>
        </body>
        </html>
        """.formatted(otp);
    }

    private String buildPaymentStatusTemplate(
            String userName,
            String orderId,
            String paymentId,
            String amount,
            String status) {

        String color = switch (status.toUpperCase()) {
            case "SUCCESS" -> "#27ae60";
            case "FAILED" -> "#e74c3c";
            default -> "#f39c12";
        };

        return """
        <html>
        <body style="font-family: Arial, sans-serif; background:#f4f4f4; padding:20px; margin:0;">
        
            <div style="max-width:600px; margin:auto; background:#ffffff;
                        border-radius:10px; overflow:hidden;
                        box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <div style="background:#2c3e50; color:white; padding:20px; text-align:center;">
                    <h2 style="margin:0;">GetInTouch</h2>
                    <p style="margin:5px 0 0;">Payment Status</p>
                </div>

                <div style="padding:30px;">

                    <h3>Hello %s,</h3>

                    <p>Your payment request has been processed.</p>

                    <div style="
                        background:%s;
                        color:white;
                        padding:15px;
                        border-radius:8px;
                        font-size:22px;
                        font-weight:bold;
                        text-align:center;
                        margin:20px 0;">
                        %s
                    </div>

                    <table style="width:100%%; border-collapse:collapse;">

                        <tr>
                            <td style="padding:10px; border:1px solid #ddd;">
                                <b>Order ID</b>
                            </td>
                            <td style="padding:10px; border:1px solid #ddd;">
                                %s
                            </td>
                        </tr>

                        <tr>
                            <td style="padding:10px; border:1px solid #ddd;">
                                <b>Payment ID</b>
                            </td>
                            <td style="padding:10px; border:1px solid #ddd;">
                                %s
                            </td>
                        </tr>

                        <tr>
                            <td style="padding:10px; border:1px solid #ddd;">
                                <b>Amount</b>
                            </td>
                            <td style="padding:10px; border:1px solid #ddd;">
                                ₹%s
                            </td>
                        </tr>

                    </table>

                    <p style="margin-top:25px;">
                        Thank you for choosing <b>GetInTouch</b>.
                    </p>

                </div>

                <div style="background:#f8f8f8; padding:15px;
                            text-align:center; font-size:12px; color:#777;">
                    © 2026 GetInTouch. All Rights Reserved.
                </div>

            </div>

        </body>
        </html>
        """.formatted(
                userName,
                color,
                status,
                orderId,
                paymentId,
                amount
        );
    }
}
