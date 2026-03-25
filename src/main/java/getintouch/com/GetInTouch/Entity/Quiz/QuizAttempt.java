package getintouch.com.GetInTouch.Entity.Quiz;

import getintouch.com.GetInTouch.Entity.User.User;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private int totalQuestions;
    private int attemptedQuestions;
    private int correctAnswers;
    private int wrongAnswers;

    private int score;
    private int totalMarks;
    private double percentage;

    @Enumerated(EnumType.STRING)
    private ResultStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private boolean submitted;
}
