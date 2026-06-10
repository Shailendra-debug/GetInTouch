package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Razorpay.Payment;
import getintouch.com.GetInTouch.Entity.Razorpay.PaymentStatus;
import getintouch.com.GetInTouch.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByUser(User user);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByRazorpayOrderIdAndStatus(
            String razorpayOrderId,
            PaymentStatus status
    );
}