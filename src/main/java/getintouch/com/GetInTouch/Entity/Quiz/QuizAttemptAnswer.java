package getintouch.com.GetInTouch.Entity.Quiz;

import getintouch.com.GetInTouch.Entity.Question.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempt_answers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"attempt_id","question_id"}))
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class QuizAttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
    @ElementCollection
    @CollectionTable(
            name = "attempt_selected_indexes",
            joinColumns = @JoinColumn(name = "answer_id")
    )
    private List<Integer> selectedIndexes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "attempt_correct_indexes",
            joinColumns = @JoinColumn(name = "answer_id")
    )
    private List<Integer> correctIndexes = new ArrayList<>();
    private boolean correct;
    private int marksObtained;
}
