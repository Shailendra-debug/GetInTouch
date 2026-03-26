package getintouch.com.GetInTouch.DTO.Chat;

import getintouch.com.GetInTouch.Entity.Chat.MessageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Chat message response")
public class ChatMessageResponseDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
}