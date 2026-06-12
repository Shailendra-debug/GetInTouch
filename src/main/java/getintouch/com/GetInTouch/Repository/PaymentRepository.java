package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Razorpay.Payment;
import getintouch.com.GetInTouch.Entity.Razorpay.PaymentStatus;
import getintouch.com.GetInTouch.Entity.User.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.razorpayOrderId = :orderId")
    Optional<Payment> findByRazorpayOrderIdForUpdate(@Param("orderId") String orderId);

    // Use Entity Graphs or JOIN FETCH to prevent N+1 issues when querying lists
    @Query("SELECT p FROM Payment p JOIN FETCH p.user JOIN FETCH p.note WHERE p.user.id = :userId")
    List<Payment> findByUserIdWithRelationships(@Param("userId") Long userId);
}