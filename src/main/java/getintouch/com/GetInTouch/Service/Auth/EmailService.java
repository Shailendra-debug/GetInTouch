package getintouch.com.GetInTouch.Service.Auth;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendMail(String to, String subject, String message) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);

        mailSender.send(mail);
    }

    public void sendOtp(String to, String otp) {

        try {
            String htmlContent = """
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
                padding: 20px;
            }
            .container {
                max-width: 500px;
                margin: auto;
                background: #ffffff;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                text-align: center;
            }
            .otp {
                font-size: 28px;
                font-weight: bold;
                color: #2c3e50;
                margin: 20px 0;
                letter-spacing: 5px;
            }
            .footer {
                font-size: 12px;
                color: #888;
                margin-top: 20px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2>🔐 Password Reset</h2>
            <p>We received a request to reset your password.</p>

            <p>Your OTP is:</p>
            <div class="otp">%s</div>

            <p>This OTP is valid for <b>5 minutes</b>.</p>

            <p>If you didn't request this, you can safely ignore this email.</p>

            <div class="footer">
                © 2026 Your App. All rights reserved.
            </div>
        </div>
    </body>
    </html>
    """.formatted(otp);
            Resend resend = new Resend("re_TxmxrL4z_81UTRk41LdnMWspaknnAVyzn");
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("getintouch790@resend.dev")
                    .to(to)
                    .subject("Reset Your Password")
                    .html(htmlContent)
                    .build();

            resend.emails().send(params);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendRegisterOtp(String to, String otp) {

        try {

            String htmlContent = """
<!DOCTYPE html>
<html>
<head>
</head>
<body style="font-family: Arial, sans-serif; background-color:#f4f4f4; padding:20px; margin:0;">

    <div style="max-width:500px; margin:auto; background:#ffffff; padding:20px; border-radius:10px; text-align:center; box-shadow:0 2px 10px rgba(0,0,0,0.1);">

        <!-- ✅ TITLE -->
        <h2 style="margin:10px 0;">🎉 Welcome to GetInTouch</h2>
        <p>Thanks for registering!</p>

        <!-- ✅ OTP -->
        <p>Your verification OTP is:</p>
        <div style="font-size:30px; font-weight:bold; color:#27ae60; letter-spacing:6px; margin:20px 0;">
            %s
        </div>

        <!-- ✅ INFO -->
        <p>This OTP is valid for <b>5 minutes</b>.</p>
        <p>Please enter this OTP to activate your account.</p>

        <!-- ✅ FOOTER -->
        <div style="font-size:12px; color:#888; margin-top:20px;">
            © 2026 GetInTouch. All rights reserved.
        </div>

    </div>

</body>
</html>
""".formatted(otp);

            Resend resend = new Resend("re_TxmxrL4z_81UTRk41LdnMWspaknnAVyzn");

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("getintouch790@resend.dev")
                    .to(to)
                    .subject("Verify Your Email")
                    .html(htmlContent)
                    .build();

            resend.emails().send(params);

        } catch (Exception e) {
            throw new BadRequestException("Failed to send OTP email"+e);
        }
    }

}
