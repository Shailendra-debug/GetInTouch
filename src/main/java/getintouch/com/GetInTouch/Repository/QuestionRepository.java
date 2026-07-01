package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // =========================================================================
    // SINGLE ENTITY FETCHES
    // =========================================================================

    @EntityGraph(attributePaths = {"chapter"})
    @Query("SELECT q FROM Question q WHERE q.id = :id AND q.chapter.active = true")
    Optional<Question> findByIdWithChapter(@Param("id") Long id);

    @Query("SELECT DISTINCT q FROM Question q " +
            "LEFT JOIN FETCH q.chapter c " +
            "LEFT JOIN FETCH q.options " +
            "LEFT JOIN FETCH q.correct " +
            "WHERE q.id = :id AND c.active = true")
    Optional<Question> findCompleteQuestionById(@Param("id") Long id);

    // =========================================================================
    // LIST QUERIES (Filtered strictly by Active Chapters)
    // =========================================================================

    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByChapterIdAndChapterActiveTrue(Long chapterId);

    // Eagerly loads the chapter relationship while filtering for active questions
    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByActiveTrue();

    // Eagerly loads the chapter relationship while filtering for inactive questions
    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByActiveIsFalse();

    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByDifficultyAndChapterActiveTrue(Difficulty difficulty);

    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByTypeAndChapterActiveTrue(QuestionType type);

    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByChapterIdAndDifficultyAndTypeAndChapterActiveTrue(
            Long chapterId,
            Difficulty difficulty,
            QuestionType type
    );

    // =========================================================================
    // MULTIPLE CHAPTER FETCHES
    // =========================================================================

    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByChapterIdInAndChapterActiveTrue(List<Long> chapterIds);

    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByChapterIdInAndDifficultyAndChapterActiveTrue(
            List<Long> chapterIds,
            Difficulty difficulty
    );

    // =========================================================================
    // SEARCH QUERIES
    // =========================================================================

    @EntityGraph(attributePaths = {"chapter"})
    @Query("""
            SELECT q FROM Question q
            WHERE LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%'))
            AND q.chapter.active = true
            """)
    List<Question> searchByKeyword(@Param("keyword") String keyword);

    // =========================================================================
    // RANDOMIZATION (Optimized via ID Fetching)
    // =========================================================================

    /**
     * Native queries bypass BOTH Question and Chapter @SQLRestrictions.
     * We MUST explicitly JOIN the chapters table to verify c.active = true.
     */
    @Query(value = """
            SELECT q.id FROM questions q
            JOIN chapters c ON q.chapter_id = c.id
            WHERE q.chapter_id = :chapterId 
            AND q.active = true 
            AND c.active = true
            ORDER BY RAND()
            LIMIT :limit
            """, nativeQuery = true)
    List<Long> getRandomQuestionIds(@Param("chapterId") Long chapterId, @Param("limit") int limit);

    // =========================================================================
    // COUNT QUERIES
    // =========================================================================

    long countByChapterIdAndChapterActiveTrue(Long chapterId);

    long countByDifficultyAndChapterActiveTrue(Difficulty difficulty);

    long countByTypeAndChapterActiveTrue(QuestionType type);

    long countByChapterIdAndDifficultyAndChapterActiveTrue(Long chapterId, Difficulty difficulty);
    // =========================================================================
    // ACTIVE / INACTIVE MANAGEMENT (Admin Trash Bin)
    // =========================================================================

    @Query(value = "SELECT * FROM questions WHERE chapter_id = :chapterId AND active = false", nativeQuery = true)
    List<Question> findDeactivatedQuestionsByChapterId(@Param("chapterId") Long chapterId);

    @Query(value = "SELECT * FROM questions WHERE active = false", nativeQuery = true)
    List<Question> findAllDeactivatedQuestions();

    @Modifying
    @Query(value = "UPDATE questions SET active = true WHERE id = :id", nativeQuery = true)
    void activateById(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM questions WHERE id = :id", nativeQuery = true)
    void hardDeleteById(@Param("id") Long id);

    /**
     * Checks if a question with this exact text already exists globally across the app.
     * Note: Because of your @SQLRestriction, this will only check ACTIVE questions.
     */
    boolean existsByQuestion(String question);

    /**
     * Recommended: Checks if a question exists specifically within the target chapter.
     * This allows the same generic question (e.g., "What is the capital?") to exist in different chapters.
     */
    boolean existsByQuestionAndChapterId(String question, Long chapterId);

    @EntityGraph(attributePaths = {"chapter"})
    List<Question> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"chapter"})
    @Query("SELECT q FROM Question q")
    List<Question> findAllWithChapter();
}