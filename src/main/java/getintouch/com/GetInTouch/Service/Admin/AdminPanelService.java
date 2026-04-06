package getintouch.com.GetInTouch.Service.Admin;

import getintouch.com.GetInTouch.Entity.Note.OrderStatus;
import getintouch.com.GetInTouch.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPanelService {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final OrderRepository orderRepository;
    private final ChatMessageRepository chatMessageRepository;

    public long getAllUser(){
        return userRepository.count();
    }

    public long PendingOrder(){
       return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    public long getAllQuiz(){
        return quizRepository.count();
    }

    public long getAllAttemptQuiz(){
        return quizAttemptRepository.count();
    }

    public long getAllSubmittedQuiz(){
        return quizAttemptRepository.countBySubmitted(true);
    }


    public Long getUnread(Long userId) {
        return chatMessageRepository.countUnread(userId);
    }
}
