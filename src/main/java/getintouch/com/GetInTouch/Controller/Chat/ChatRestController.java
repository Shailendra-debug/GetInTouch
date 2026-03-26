package getintouch.com.GetInTouch.Controller.Chat;


import getintouch.com.GetInTouch.DTO.Chat.AdminChatDTO;
import getintouch.com.GetInTouch.DTO.Chat.ChatListDTO;
import getintouch.com.GetInTouch.DTO.Chat.ChatMessageResponseDTO;
import getintouch.com.GetInTouch.Service.Chat.ChatService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat APIs", description = "User chat operations")
@SecurityRequirement(name = "bearerAuth")
public class ChatRestController {

    private final ChatService chatService;

    // 🔥 1. Get Chat History
    @Operation(summary = "Get chat messages with a user")
    @GetMapping("/{userId}")
    public Page<ChatMessageResponseDTO> getChat(
            @PathVariable Long userId,
            Pageable pageable) {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return chatService.getChat(currentUser, userId, pageable);
    }

    // 🔥 2. Mark Messages as Read
    @Operation(summary = "Mark messages as read")
    @PostMapping("/mark-read/{senderId}")
    public void markRead(@PathVariable Long senderId) {

        Long currentUser = SecurityUtil.getCurrentUserId();

        chatService.markAsRead(currentUser, senderId);
    }

    // 🔥 3. Get Total Unread Count
    @Operation(summary = "Get total unread messages")
    @GetMapping("/unread")
    public Long getUnread() {

        Long userId = SecurityUtil.getCurrentUserId();

        return chatService.getUnread(userId);
    }

    // 🔥 4. Chat List (WhatsApp style)
    @Operation(summary = "Get chat list")
    @GetMapping("/chat-list")
    public List<ChatListDTO> getChatList() {

        Long userId = SecurityUtil.getCurrentUserId();

        return chatService.getChatList(userId);
    }

    // 🔥 5. Admin Dashboard
    @Operation(summary = "Admin support dashboard")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminChatDTO> getAdminChats() {

        Long adminId = SecurityUtil.getCurrentUserId();

        return chatService.getAdminChats(adminId);
    }
}