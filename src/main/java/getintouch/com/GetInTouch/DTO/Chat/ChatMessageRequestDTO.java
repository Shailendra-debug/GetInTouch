package getintouch.com.GetInTouch.DTO.Chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Send message request")
public class ChatMessageRequestDTO {

    @Schema(example = "2")
    private Long receiverId;

    @Schema(example = "Hello Admin")
    private String content;
}