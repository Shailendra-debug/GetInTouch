package getintouch.com.GetInTouch.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {

        System.out.println("KEY ID = " + keyId);
        System.out.println("SECRET PRESENT = " + (keySecret != null));

        return new RazorpayClient(keyId, keySecret);
    }
}
