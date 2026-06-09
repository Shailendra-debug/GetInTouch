package getintouch.com.GetInTouch.Entity.Note;

import getintouch.com.GetInTouch.Entity.User.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Notes note;

    private LocalDateTime purchaseDate;
    private BigDecimal amountPaid;
    private String paymentStatus;
}
