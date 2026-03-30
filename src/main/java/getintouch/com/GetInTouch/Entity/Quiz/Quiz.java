package getintouch.com.GetInTouch.Entity.Quiz;

import getintouch.com.GetInTouch.Entity.Question.Question;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(
        name = "quizzes",
        indexes = @Index(name = "idx_quiz_course", columnList = "course_id")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private int timeLimit;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizType type;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(nullable = false)
    private int totalMarks;

    @Column(nullable = false)
    private int passMarks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;

    @PrePersist
    @PreUpdate
    private void beforeSave() {
        this.totalMarks = questions == null ? 0 :
                questions.stream().mapToInt(Question::getMarks).sum();

        if ((type == QuizType.LIVE || type == QuizType.EXAM)
                && (startTime == null || endTime == null)) {
            throw new IllegalStateException("LIVE / EXAM quiz must have schedule");
        }

        if (type == QuizType.PRACTICE && (startTime != null || endTime != null)) {
            throw new IllegalStateException("PRACTICE quiz must not have schedule");
        }
    }

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
