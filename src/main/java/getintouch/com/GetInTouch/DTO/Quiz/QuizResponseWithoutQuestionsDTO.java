package getintouch.com.GetInTouch.DTO.Quiz;


import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

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
    private String type;

    /* ---------- COURSE ---------- */
    private CourseResponseDTO course;

    /* ---------- COUNTS ---------- */
    private int totalQuestions;
    private int totalMarks;
    private int passingMarks;

    /* ---------- SCHEDULE ---------- */
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /* ---------- AUDIT ---------- */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
