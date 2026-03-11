package getintouch.com.GetInTouch.Entity.Question;

import getintouch.com.GetInTouch.Entity.Quiz.Course;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    /* ---------- COURSE MAPPING ---------- */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /* ---------- OPTIONS ---------- */
    @ElementCollection
    @CollectionTable(
            name = "question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text", nullable = false)
    private List<String> options;

    /* ---------- CORRECT ANSWER INDEXES ---------- */
    @ElementCollection
    @CollectionTable(
            name = "question_correct_indexes",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "correct_index", nullable = false)
    private List<Integer> correct;

    @Column(length = 1000)
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Difficulty difficulty = Difficulty.EASY;

    @Column(nullable = false)
    @Builder.Default
    private int marks = 1;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
