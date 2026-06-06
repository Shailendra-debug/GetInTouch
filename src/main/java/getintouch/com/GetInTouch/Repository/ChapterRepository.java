package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // 1. Single Fetch: Fetches active chapter and joins its parent Paper
    @EntityGraph(attributePaths = {"paper"})
    Optional<Chapter> findByIdAndActiveTrue(Long id);

    // If your Paper also has a Course relationship that you map in your DTOs,
    // you can fetch nested relationships like this instead:
    // @EntityGraph(attributePaths = {"paper", "paper.course"})

    // 2. Fixes N+1 when retrieving all active chapters
    @EntityGraph(attributePaths = {"paper"})
    List<Chapter> findByActiveTrueOrderByChapterNumberAsc();

    // 3. Fixes N+1 when retrieving all chapters of a paper
    @EntityGraph(attributePaths = {"paper"})
    List<Chapter> findByPaperIdOrderByChapterNumberAsc(Long paperId);

    // 4. Fixes N+1 when retrieving active chapters of a paper
    @EntityGraph(attributePaths = {"paper"})
    List<Chapter> findByPaperIdAndActiveTrueOrderByChapterNumberAsc(Long paperId);

    // 5. Fixes N+1 when searching single unique chapter records
    @EntityGraph(attributePaths = {"paper"})
    Optional<Chapter> findByPaperIdAndChapterNumber(Long paperId, Long chapterNumber);

    // 6. Fix for write-operation validation paths (Add this for your updateChapter service methods)
    @EntityGraph(attributePaths = {"paper"})
    Optional<Chapter> findWithPaperById(Long id);

    // 7. Added derived DB-level Search query to prevent any in-memory stream filtering bottlenecks
    @EntityGraph(attributePaths = {"paper"})
    List<Chapter> findByActiveTrueAndTitleContainingIgnoreCaseOrderByChapterNumberAsc(String keyword);


    // --- Validation Queries (Left as-is: already fully optimized) ---

    // Check duplicate chapter title within a paper
    boolean existsByPaperIdAndTitleIgnoreCase(Long paperId, String title);

    // Check duplicate chapter number within a paper
    boolean existsByPaperIdAndChapterNumber(Long paperId, Long chapterNumber);
}
