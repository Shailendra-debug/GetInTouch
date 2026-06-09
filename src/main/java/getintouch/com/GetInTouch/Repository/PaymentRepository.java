package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Razorpay.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Crucial for looking up the payment during verification
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
