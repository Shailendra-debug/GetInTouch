package getintouch.com.GetInTouch.Repository;



import getintouch.com.GetInTouch.Entity.Quiz.Quiz;
import getintouch.com.GetInTouch.Entity.Quiz.QuizType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /* =====================================================
       LIST QUIZZES (WITHOUT QUESTIONS)
       Fast & lightweight
       ===================================================== */
    List<Quiz> findByActiveTrue();

    List<Quiz> findByCourse_Id(Long courseId);

    List<Quiz> findByType(QuizType type);

    /* =====================================================
       SINGLE QUIZ (WITH QUESTIONS)
       Avoids N+1 using EntityGraph
       ===================================================== */
    @EntityGraph(attributePaths = {"questions", "course"})
    Optional<Quiz> findWithQuestionsById(Long id);

    /* =====================================================
       SCHEDULED QUIZZES
       ===================================================== */
    @Query("""
        SELECT q FROM Quiz q
        WHERE q.type = 'SCHEDULED'
          AND q.active = true
          AND q.startTime <= CURRENT_TIMESTAMP
          AND q.endTime >= CURRENT_TIMESTAMP
    """)
    List<Quiz> findActiveScheduledQuizzes();

    /* =====================================================
       PRACTICE QUIZZES
       ===================================================== */
    List<Quiz> findByTypeAndActiveTrue(QuizType type);

}

