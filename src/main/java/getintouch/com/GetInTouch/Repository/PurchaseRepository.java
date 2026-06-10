package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Entity.Note.Purchase;
import getintouch.com.GetInTouch.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // 1. Find all purchases made by a specific user (Useful for order history)
    List<Purchase> findByUser(User user);

    List<Purchase> findByUserId(Long userId);

    // 2. FOR OPTION A: Check if a user has bought a specific individual note
    boolean existsByUserAndNote(User user, Notes note);

    boolean existsByUserIdAndNoteId(Long userId, Long noteId);

    // 3. OPTIONAL: Find a specific purchase record by user and note identifiers
    Optional<Purchase> findByUserIdAndNoteId(Long userId, Long noteId);

    // Fetches all successful purchases for a user sorted by the latest purchase date
    List<Purchase> findByUserIdAndPaymentStatusOrderByPurchaseDateDesc(Long userId, String paymentStatus);

    @Query("SELECT p.note.id FROM Purchase p WHERE p.user.id = :userId AND p.paymentStatus = 'SUCCESS'")
    Set<Long> findPurchasedNoteIdsByUserId(@Param("userId") Long userId);




}
