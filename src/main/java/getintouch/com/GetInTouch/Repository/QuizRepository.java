package getintouch.com.GetInTouch.Repository;



import getintouch.com.GetInTouch.Entity.Quiz.Quiz;
import getintouch.com.GetInTouch.Entity.Quiz.QuizType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // =========================================================================
    // SINGLE QUIZ (WITH QUESTIONS) - Optimized for Test-Taking
    // =========================================================================

    /**
     * Eagerly loads the Chapter AND the entire ManyToMany Questions list.
     * Crucial for the "Start Quiz" endpoint to prevent severe N+1 database crashing.
     */
    @EntityGraph(attributePaths = {"chapter", "questions"})
    @Query("SELECT q FROM Quiz q WHERE q.id = :id")
    Optional<Quiz> findCompleteQuizById(@Param("id") Long id);

    /**
     * Lightweight fetch for metadata (ignores the massive questions list).
     */
    @EntityGraph(attributePaths = {"chapter"})
    @Query("SELECT q FROM Quiz q WHERE q.id = :id")
    Optional<Quiz> findByIdWithChapter(@Param("id") Long id);

    // =========================================================================
    // LIST QUIZZES (Lightweight, excludes Questions array)
    // =========================================================================

    @EntityGraph(attributePaths = {"chapter"})
    List<Quiz> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"chapter"})
    List<Quiz> findByChapterIdOrderByCreatedAtDesc(Long chapterId);

    @EntityGraph(attributePaths = {"chapter"})
    List<Quiz> findByTypeOrderByCreatedAtDesc(QuizType type);

    // =========================================================================
    // SCHEDULED QUIZZES (LIVE / EXAM)
    // =========================================================================

    /**
     * Finds quizzes that are currently running.
     * Note: No need for `q.active = true` here because @SQLRestriction applies it automatically!
     */
    @EntityGraph(attributePaths = {"chapter"})
    @Query("""
        SELECT q FROM Quiz q
        WHERE q.type IN ('LIVE', 'EXAM')
          AND q.startTime <= CURRENT_TIMESTAMP
          AND q.endTime >= CURRENT_TIMESTAMP
    """)
    List<Quiz> findCurrentlyRunningQuizzes();

    // =========================================================================
    // VALIDATION / EXISTS QUERIES
    // =========================================================================

    boolean existsByChapterIdAndTitleIgnoreCase(Long chapterId, String title);

    // =========================================================================
    // ADMIN / TRASH BIN MANAGEMENT (Native Queries bypass @SQLRestriction)
    // =========================================================================

    /**
     * Fetches all deactivated (soft-deleted) quizzes for a specific chapter.
     */
    @Query(value = "SELECT * FROM quizzes WHERE chapter_id = :chapterId AND active = false", nativeQuery = true)
    List<Quiz> findDeactivatedQuizzesByChapterId(@Param("chapterId") Long chapterId);

    /**
     * Fetches all deactivated quizzes across the platform.
     */
    @Query(value = "SELECT * FROM quizzes WHERE active = false", nativeQuery = true)
    List<Quiz> findAllDeactivatedQuizzes();

    /**
     * Reactivates a soft-deleted quiz.
     */
    @Modifying
    @Query(value = "UPDATE quizzes SET active = true WHERE id = :id", nativeQuery = true)
    void activateById(@Param("id") Long id);

    /**
     * Permanently wipes the quiz from the database.
     */
    @Modifying
    @Query(value = "DELETE FROM quizzes WHERE id = :id", nativeQuery = true)
    void hardDeleteById(@Param("id") Long id);
}