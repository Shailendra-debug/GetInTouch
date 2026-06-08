package getintouch.com.GetInTouch.DTO.Quiz;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponseWithoutQuestionsDTO {

    private Long id;
    private String title;
    private String description;
    private int timeLimit;
    private boolean active;


    private String thumbnail;

    @Builder.Default
    private Boolean showResult = true;

    private String type;

    /* ---------- FULL CHAPTER OBJECT ---------- */
    private ChapterResponseDTO chapter;

    private Long quizId;

    /* ---------- COUNTS ---------- */
    private int totalQuestions;
    private int totalMarks;
    private int passingMarks;

    /* ---------- SCHEDULE ---------- */
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    /* ---------- AUDIT ---------- */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}