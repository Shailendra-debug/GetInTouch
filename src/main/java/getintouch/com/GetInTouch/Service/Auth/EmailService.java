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

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    private static final String FROM_EMAIL = "support@skushwaha.in";




    public void sendRegisterOtp(String to, String otp) {
        sendEmail(to, "Verify Your Email", buildRegisterTemplate(otp));
    }

    public void sendResetOtp(String to, String otp) {
        sendEmail(to, "Reset Your Password", buildResetTemplate(otp));
    }

    public void sendPaymentStatusEmail(String to, boolean isApproved) {

        String subject = isApproved ? "Payment Approved 🎉" : "Payment Failed ❌";

        String title = isApproved ? "🎉 Payment Successful" : "❌ Payment Failed";

        String message = isApproved
                ? "Your payment has been approved successfully. You can now access your notes."
                : "Your payment could not be processed. Please try again.";

        String color = isApproved ? "#27ae60" : "#e74c3c";

        String htmlContent = """
    <html>
    <body style="font-family: Arial; background:#f4f4f4; padding:20px;">
        <div style="max-width:500px;margin:auto;background:#fff;padding:20px;border-radius:10px;text-align:center;">

            <h2 style="color:%s;">%s</h2>

            <p>%s</p>

            <div style="font-size:12px;color:#888;margin-top:20px;">
                © 2026 GetInTouch
            </div>

        </div>
    </body>
    </html>
    """.formatted(color, title, message);

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
}