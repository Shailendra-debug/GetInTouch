package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Quiz.QuizAttempt;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizAttemptAnswerRepository
        extends JpaRepository<QuizAttemptAnswer, Long> {

    @Query("""
        SELECT a FROM QuizAttemptAnswer a
        JOIN FETCH a.question
        WHERE a.attempt.id = :attemptId
    """)
    List<QuizAttemptAnswer> findByAttemptIdWithQuestion(@Param("attemptId") Long attemptId);

}
