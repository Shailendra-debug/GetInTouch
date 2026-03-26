package getintouch.com.GetInTouch.Repository;



import getintouch.com.GetInTouch.Entity.Chat.ChatMessage;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE (m.senderId = :u1 AND m.receiverId = :u2)
           OR (m.senderId = :u2 AND m.receiverId = :u1)
        ORDER BY m.createdAt DESC
    """)
    Page<ChatMessage> findChat(Long u1, Long u2, Pageable pageable);

    @Query("""
        SELECT COUNT(m) FROM ChatMessage m
        WHERE m.senderId = :senderId
        AND m.receiverId = :receiverId
        AND m.status != 'SEEN'
    """)
    Long countUnreadFromUser(Long senderId, Long receiverId);

    @Query("""
        SELECT COUNT(m) FROM ChatMessage m
        WHERE m.receiverId = :userId
        AND m.status != 'SEEN'
    """)
    Long countUnread(Long userId);

    @Modifying
    @Query("""
        UPDATE ChatMessage m
        SET m.status = 'SEEN'
        WHERE m.senderId = :senderId
        AND m.receiverId = :receiverId
    """)
    int markAsSeen(Long senderId, Long receiverId);

    @Query("""
        SELECT DISTINCT 
        CASE 
            WHEN m.senderId = :user THEN m.receiverId
            ELSE m.senderId
        END
        FROM ChatMessage m
        WHERE m.senderId = :user OR m.receiverId = :user
    """)
    List<Long> findChatUsers(Long user);

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE (m.senderId = :u1 AND m.receiverId = :u2)
           OR (m.senderId = :u2 AND m.receiverId = :u1)
        ORDER BY m.createdAt DESC
    """)
    List<ChatMessage> findTopMessage(Long u1, Long u2, Pageable pageable);
}