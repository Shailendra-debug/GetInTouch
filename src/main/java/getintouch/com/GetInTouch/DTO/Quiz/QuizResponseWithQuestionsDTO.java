package getintouch.com.GetInTouch.DTO.Quiz;

import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseForQuizDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponseWithQuestionsDTO {

    private Long id;
    private String title;
    private String description;
    private int timeLimit;
    private boolean active;
    private String type;

    /* ---------- COURSE ---------- */
    private CourseResponseDTO course;

    /* ---------- QUESTIONS ---------- */
    private List<QuestionResponseForQuizDTO> questions;
    private int totalQuestions;

    private Long attemptId;//Quiz AttemptId

    /* ---------- MARKS ---------- */
    private int totalMarks;
    private int passingMarks;

    /* ---------- SCHEDULE ---------- */
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /* ---------- AUDIT ---------- */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
