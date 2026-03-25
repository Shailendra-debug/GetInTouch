package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Quiz.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, Long> {

    /* ---------- START QUIZ ---------- */
    // prevent multiple attempts by same user for same quiz
    Optional<QuizAttempt> findByQuizIdAndUserId(Long quizId, Long userId);

    /* ---------- USER HISTORY ---------- */
    // get all quiz attempts of a user
    List<QuizAttempt> findByUserId(Long userId);

    /* ---------- QUIZ HISTORY ---------- */
    // get all attempts of a quiz (leaderboard use)
    List<QuizAttempt> findByQuizId(Long quizId);
}