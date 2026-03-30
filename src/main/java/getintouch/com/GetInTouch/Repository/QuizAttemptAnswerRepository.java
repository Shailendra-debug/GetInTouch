package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Quiz.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptAnswerRepository
        extends JpaRepository<QuizAttemptAnswer, Long> {

    List<QuizAttemptAnswer> findByAttemptId(Long attemptId);
}
