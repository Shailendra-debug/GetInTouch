package getintouch.com.GetInTouch.Entity.Chat;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages",
        indexes = {
                @Index(name = "idx_sender_receiver", columnList = "senderId,receiverId"),
                @Index(name = "idx_receiver_status", columnList = "receiverId,status")
        })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    private LocalDateTime createdAt;
}
