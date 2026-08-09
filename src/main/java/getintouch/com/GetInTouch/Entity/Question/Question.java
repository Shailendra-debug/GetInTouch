package getintouch.com.GetInTouch.Entity.Question;

import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    /* ---------- CHAPTER MAPPING ---------- */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    /* ---------- OPTIONS ---------- */

    @ElementCollection
    @CollectionTable(
            name = "question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    private List<Option> options;

    /* ---------- CORRECT ANSWER INDEXES ---------- */

    @ElementCollection
    @CollectionTable(
            name = "question_correct_indexes",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "correct_index", nullable = false)
    private Set<Integer> correct;

    /* ---------- IMAGE QUESTION ---------- */

    @Builder.Default
    @Column(nullable = false)
    private boolean imageQuestion = false;

    private String imageUrl;

    /* ---------- EXPLANATION ---------- */

    @Column(length = 1000)
    private String explanation;

    /* ---------- TYPE ---------- */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    /* ---------- DIFFICULTY ---------- */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Difficulty difficulty = Difficulty.EASY;

    /* ---------- MARKS ---------- */

    @Column(nullable = false)
    @Builder.Default
    private int marks = 1;

    /* ---------- ACTIVE ---------- */

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /* ---------- AUDITING ---------- */

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}