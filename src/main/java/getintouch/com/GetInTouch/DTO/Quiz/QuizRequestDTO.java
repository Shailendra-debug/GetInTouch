package getintouch.com.GetInTouch.DTO.Quiz;

import getintouch.com.GetInTouch.Entity.Quiz.QuizType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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

    private Boolean showResult;


    private String thumbnail;

    @NotNull
    private QuizType type;

    /* ---------- COURSE ---------- */
    @NotNull
    private Long chapterId;

    @NotNull
    private int passingMarks;

    /* ---------- SCHEDULE ---------- */
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;



    /* ---------- QUESTIONS ---------- */
    @NotEmpty
    private List<Long> questionIds;
}
