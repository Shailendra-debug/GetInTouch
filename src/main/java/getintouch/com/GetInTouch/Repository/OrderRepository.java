package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Entity.Note.Order;
import getintouch.com.GetInTouch.Entity.Note.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    boolean existsByUserIdAndNoteIdAndStatus(Long userId, Long noteId, OrderStatus status);

    @Query("""
    SELECT o.note.id FROM Order o
    WHERE o.userId = :userId AND o.status = 'APPROVED'
""")
    List<Long> findApprovedNoteIds(Long userId);

    @Query("""
    SELECT n FROM Order o
    JOIN o.note n
    WHERE o.userId = :userId
    AND n.id = :noteId
    AND o.status = 'APPROVED'
""")
    Optional<Notes> findApprovedNote(Long userId, Long noteId);

    @Query("""
    SELECT n FROM Order o
    JOIN o.note n
    WHERE o.userId = :userId
    AND o.status = 'APPROVED'
""")
    List<Notes> findAllApprovedNotes(Long userId);

    long countByStatus(OrderStatus status);
}
