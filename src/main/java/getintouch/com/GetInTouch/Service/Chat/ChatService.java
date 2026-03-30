package getintouch.com.GetInTouch.Service.Chat;

import getintouch.com.GetInTouch.DTO.Chat.AdminChatDTO;
import getintouch.com.GetInTouch.DTO.Chat.ChatListDTO;
import getintouch.com.GetInTouch.DTO.Chat.ChatMessageRequestDTO;
import getintouch.com.GetInTouch.DTO.Chat.ChatMessageResponseDTO;
import getintouch.com.GetInTouch.Entity.Chat.ChatMessage;
import getintouch.com.GetInTouch.Entity.Chat.MessageStatus;
import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.ChatMessageRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository repo;
    private final UserRepository userRepo;

    public ChatMessageResponseDTO sendMessage(Long senderId, ChatMessageRequestDTO dto) {

        User sender = userRepo.findById(senderId).orElseThrow();
        User receiver = userRepo.findById(dto.getReceiverId()).orElseThrow();

        if (sender.getRole() == Role.USER && receiver.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin chat allowed");
        }

        ChatMessage msg = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(dto.getReceiverId())
                .content(dto.getContent())
                .status(MessageStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();

        return map(repo.save(msg));
    }

    public Page<ChatMessageResponseDTO> getChat(Long u1, Long u2, Pageable pageable) {
        return repo.findChat(u1, u2, pageable).map(this::map);
    }

    @Transactional
    public void markAsRead(Long currentUser, Long senderId) {
        repo.markAsSeen(senderId, currentUser);
    }

    public Long getUnread(Long userId) {
        return repo.countUnread(userId);
    }

    public List<ChatListDTO> getChatList(Long userId) {

        List<Long> users = repo.findChatUsers(userId);

        return users.stream().map(other -> {

                    ChatMessage last = repo.findTopMessage(userId, other, PageRequest.of(0,1)).get(0);

                    Long unread = repo.countUnreadFromUser(other, userId);

                    return ChatListDTO.builder()
                            .userId(other)
                            .lastMessage(last.getContent())
                            .lastMessageTime(last.getCreatedAt())
                            .unreadCount(unread)
                            .build();

                }).sorted((a,b)->b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .toList();
    }

    public List<AdminChatDTO> getAdminChats(Long adminId) {

        List<Long> users = repo.findChatUsers(adminId);

        return users.stream().map(user -> {

            ChatMessage last = repo.findTopMessage(adminId, user, PageRequest.of(0,1)).get(0);

            Long unread = repo.countUnreadFromUser(user, adminId);

            return AdminChatDTO.builder()
                    .userId(user)
                    .lastMessage(last.getContent())
                    .lastMessageTime(last.getCreatedAt())
                    .unreadCount(unread)
                    .build();

        }).toList();
    }

    private ChatMessageResponseDTO map(ChatMessage m){
        return ChatMessageResponseDTO.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .receiverId(m.getReceiverId())
                .content(m.getContent())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .build();
    }
}