package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // =========================================================================
    // CORE FETCHES (Automatically ignores active = false due to @SQLRestriction)
    // =========================================================================

    /**
     * Replaces standard findById to eagerly fetch the Paper association.
     */
    @EntityGraph(attributePaths = {"paper"})
    @Query("SELECT c FROM Chapter c WHERE c.id = :id")
    Optional<Chapter> findByIdWithPaper(@Param("id") Long id);

    @EntityGraph(attributePaths = {"paper"})
    List<Chapter> findAllByOrderByChapterNumberAsc();

    @EntityGraph(attributePaths = {"paper"})
    List<Chapter> findByPaperIdOrderByChapterNumberAsc(Long paperId);

    @EntityGraph(attributePaths = {"paper"})
    Optional<Chapter> findByPaperIdAndChapterNumber(Long paperId, Long chapterNumber);

    // =========================================================================
    // SEARCH
    // =========================================================================

    @EntityGraph(attributePaths = {"paper"})
    List<Chapter> findByTitleContainingIgnoreCaseOrderByChapterNumberAsc(String keyword);

    // =========================================================================
    // VALIDATION QUERIES
    // =========================================================================

    boolean existsByPaperIdAndTitleIgnoreCase(Long paperId, String title);

    boolean existsByPaperIdAndChapterNumber(Long paperId, Long chapterNumber);

    // =========================================================================
    // ADMIN / TRASH BIN MANAGEMENT (Native Queries bypass @SQLRestriction)
    // =========================================================================

    /**
     * Fetches all deactivated (soft-deleted) chapters for a specific paper.
     */
    @Query(value = "SELECT * FROM chapters WHERE paper_id = :paperId AND active = false", nativeQuery = true)
    List<Chapter> findDeactivatedChaptersByPaperId(@Param("paperId") Long paperId);

    /**
     * Fetches all deactivated chapters across the platform.
     */
    @Query(value = "SELECT * FROM chapters WHERE active = false", nativeQuery = true)
    List<Chapter> findAllDeactivatedChapters();

    /**
     * Reactivates a soft-deleted chapter.
     */
    @Modifying
    @Query(value = "UPDATE chapters SET active = true WHERE id = :id", nativeQuery = true)
    void activateById(@Param("id") Long id);

    /**
     * Permanently wipes the chapter from the database.
     */
    @Modifying
    @Query(value = "DELETE FROM chapters WHERE id = :id", nativeQuery = true)
    void hardDeleteById(@Param("id") Long id);
}