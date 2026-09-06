package getintouch.com.GetInTouch.Repository;



import getintouch.com.GetInTouch.Entity.Quiz.Quiz;
import getintouch.com.GetInTouch.Entity.Quiz.QuizType;
import jakarta.transaction.Transactional;
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

    @EntityGraph(attributePaths = {"chapter", "questions"})
    @Query("SELECT q FROM Quiz q WHERE q.id = :id")
    Optional<Quiz> findCompleteQuizById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"chapter"})
    @Query("SELECT q FROM Quiz q WHERE q.id = :id")
    Optional<Quiz> findByIdWithChapter(@Param("id") Long id);

    // =========================================================================
    // LIST QUIZZES (Lightweight, excludes Questions array)
    // =========================================================================

    @EntityGraph(attributePaths = {"chapter"})
    List<Quiz> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"chapter"})
    List<Quiz> findByChapterIdAndActiveTrueOrderByCreatedAtDesc(Long chapterId);
    @EntityGraph(attributePaths = {"chapter"})
    List<Quiz> findByTypeOrderByCreatedAtDesc(QuizType type);



    // =========================================================================
    // SCHEDULED QUIZZES (LIVE / EXAM)
    // =========================================================================

    @EntityGraph(attributePaths = {"chapter"})
    @Query("""
        SELECT q FROM Quiz q
        WHERE q.type IN ('LIVE', 'EXAM')
          AND q.startTime <= CURRENT_TIMESTAMP
          AND q.endTime >= CURRENT_TIMESTAMP
    """)
    List<Quiz> findCurrentlyRunningQuizzes();

    // =========================================================================
    // PAPER-WISE QUIZZES
    // =========================================================================

    /**
     * Fetches all active quizzes belonging to a specific paper, ordered by latest.
     * Uses EntityGraph to fetch the associated chapter in a single join query.
     */
    List<Quiz> findByPaperIdAndChapterIsNullOrderByCreatedAtDesc(Long paperId);

    // =========================================================================
    // VALIDATION / EXISTS QUERIES
    // =========================================================================

    boolean existsByChapterIdAndTitleIgnoreCase(Long chapterId, String title);

    // =========================================================================
    // ADMIN / TRASH BIN MANAGEMENT (Native Queries bypass @SQLRestriction)
    // =========================================================================

    @Query(value = "SELECT * FROM quizzes WHERE chapter_id = :chapterId AND active = false", nativeQuery = true)
    List<Quiz> findDeactivatedQuizzesByChapterId(@Param("chapterId") Long chapterId);

    @Query(value = "SELECT * FROM quizzes WHERE active = false", nativeQuery = true)
    List<Quiz> findAllDeactivatedQuizzes();

    List<Quiz> findByActiveTrue();

    /**
     * Reactivates a soft-deleted quiz.
     * FIXED: Added clearAutomatically to prevent stale Hibernate cache.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE quizzes SET active = true WHERE id = :id", nativeQuery = true)
    void activateById(@Param("id") Long id);

    /**
     * Permanently wipes the quiz from the database.
     * FIXED: Added query to clean the join table first, preventing Foreign Key errors.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
        DELETE FROM quiz_questions WHERE quiz_id = :id;
        DELETE FROM quizzes WHERE id = :id;
    """, nativeQuery = true)
    void hardDeleteById(@Param("id") Long id);


    @Modifying
    @Query(
            value = "DELETE FROM quiz_questions " +
                    "WHERE quiz_id = :quizId " +
                    "AND question_id = :questionId",
            nativeQuery = true
    )
    int deleteQuestionFromQuiz(
            @Param("quizId") Long quizId,
            @Param("questionId") Long questionId
    );
}