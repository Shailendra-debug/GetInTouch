package getintouch.com.GetInTouch.DTO.Admin;


import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AdminPanelDtu {
    private long totalST;
    private long totalQuiz;
    private long totalQuizAttempt;
    private long totalQuizSubmit;
    private long totalPendingNotesOrder;
}
