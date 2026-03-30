package getintouch.com.GetInTouch.DTO.Chat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminChatDTO {

    private Long userId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
}
