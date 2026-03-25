package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /* ---------- BASIC FILTERS ---------- */

    List<Question> findByDifficulty(Difficulty difficulty);

    List<Question> findByType(QuestionType type);

    List<Question> findByDifficultyAndType(
            Difficulty difficulty,
            QuestionType type
    );

    /* ---------- EXISTS / COUNT (USEFUL) ---------- */

    boolean existsByQuestion(String question);

    long countByDifficulty(Difficulty difficulty);
}
