package getintouch.com.GetInTouch.Entity.Note;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes",
        indexes = {
                @Index(name = "idx_notes_active", columnList = "active"),
                @Index(name = "idx_notes_created_at", columnList = "created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title of notes
    @Column(nullable = false, length = 255)
    private String title;

    // Price (safe for money)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Description
    @Column(columnDefinition = "TEXT")
    private String description;

    // Thumbnail Image URL
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // PDF file URL
    @Column(name = "pdf_url", nullable = false)
    private String pdfUrl;

    // Payment QR Code URL (NEW FIELD)
    @Column(name = "payment_qr_url")
    private String paymentQrUrl;

    // Active status
    @Column(nullable = false)
    private Boolean active = true;

    // Created timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Updated timestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Auto timestamps
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
