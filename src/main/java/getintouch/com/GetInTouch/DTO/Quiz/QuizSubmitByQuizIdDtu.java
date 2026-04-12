package getintouch.com.GetInTouch.DTO.Quiz;

import getintouch.com.GetInTouch.Entity.Quiz.ResultStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class QuizSubmitByQuizIdDtu {
    private long userId;
    private  String fullName;
    private long attemptId;
    private double totalScore;
    private ResultStatus status;
}
