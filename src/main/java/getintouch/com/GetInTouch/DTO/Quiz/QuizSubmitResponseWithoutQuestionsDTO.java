package getintouch.com.GetInTouch.DTO.Quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
public class QuizSubmitResponseWithoutQuestionsDTO {

    private Long attemptId;
    private Long quizId;
    private Long userId;

    private int totalQuestions;
    private int attemptedQuestions;
    private int correctAnswers;
    private int wrongAnswers;

    private int score;
    private int totalMarks;
    private double percentage;

    private String status;

    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
}
