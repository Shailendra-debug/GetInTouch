package getintouch.com.GetInTouch.DTO.Quiz;

import getintouch.com.GetInTouch.Entity.Quiz.QuizType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequestDTO {

    @NotBlank
    private String title;

    private String description;

    @Min(1)
    private int timeLimit;

    private boolean active;

    @NotNull
    private QuizType type;

    /* ---------- COURSE ---------- */
    @NotNull
    private Long courseId;

    @NotNull
    private int passingMarks;

    /* ---------- SCHEDULE ---------- */
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    /* ---------- QUESTIONS ---------- */
    @NotEmpty
    private List<Long> questionIds;
}
