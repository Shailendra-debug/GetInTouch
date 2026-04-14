package getintouch.com.GetInTouch.Entity.HomePage;

import getintouch.com.GetInTouch.DTO.HomePage.Contect_Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;
    private String email;
    private String subject;

    @Column(length = 1000)
    private String message;

    private boolean isRead = false; // 👈 NEW

    private Contect_Status status = Contect_Status.PENDING; // PENDING / RESOLVED

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
