package getintouch.com.GetInTouch.Entity.Quiz;

import getintouch.com.GetInTouch.Entity.Question.Question;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(
        name = "quizzes",
        indexes = @Index(name = "idx_quiz_chapter", columnList = "chapter_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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


    private String thumbnail;

    @Column(nullable = false)
    private boolean active;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    // FIXED: Added mapping and semicolon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizType type;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

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



    @Builder.Default
    private Boolean showResult = true;

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