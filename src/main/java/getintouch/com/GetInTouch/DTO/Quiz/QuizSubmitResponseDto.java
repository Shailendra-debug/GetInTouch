package getintouch.com.GetInTouch.DTO.Quiz;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizSubmitResponseDto {

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

    private List<QuestionResultDto> results;
}

