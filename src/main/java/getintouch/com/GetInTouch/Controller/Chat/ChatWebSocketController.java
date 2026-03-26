package getintouch.com.GetInTouch.Controller.Chat;


import getintouch.com.GetInTouch.DTO.Chat.ChatMessageRequestDTO;
import getintouch.com.GetInTouch.DTO.Chat.ChatMessageResponseDTO;
import getintouch.com.GetInTouch.Service.Chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // 🔥 SEND MESSAGE
    @MessageMapping("/chat.private")
    public void sendMessage(ChatMessageRequestDTO dto, Principal principal) {

        Long senderId = Long.parseLong(principal.getName());

        ChatMessageResponseDTO response =
                chatService.sendMessage(senderId, dto);

        // send to receiver
        messagingTemplate.convertAndSendToUser(
                dto.getReceiverId().toString(),
                "/queue/messages",
                response
        );
    }

    // 🔥 READ RECEIPT
    @MessageMapping("/chat.read")
    public void markRead(Long senderId, Principal principal) {

        Long currentUser = Long.parseLong(principal.getName());

        chatService.markAsRead(currentUser, senderId);

        // notify sender that messages are seen
        messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/read",
                currentUser
        );
    }
}