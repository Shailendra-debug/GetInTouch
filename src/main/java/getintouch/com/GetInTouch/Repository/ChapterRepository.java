package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // Find active chapter by id
    Optional<Chapter> findByIdAndActiveTrue(Long id);

    // Get all active chapters sorted by chapterNumber
    List<Chapter> findByActiveTrueOrderByChapterNumberAsc();

    // Get all chapters of a paper sorted by chapterNumber
    List<Chapter> findByPaperIdOrderByChapterNumberAsc(Long paperId);

    // Get all active chapters of a paper sorted by chapterNumber
    List<Chapter> findByPaperIdAndActiveTrueOrderByChapterNumberAsc(Long paperId);

    // Check duplicate chapter title within a paper
    boolean existsByPaperIdAndTitleIgnoreCase(Long paperId, String title);

    // Check duplicate chapter number within a paper
    boolean existsByPaperIdAndChapterNumber(Long paperId, Long chapterNumber);

    // Find chapter by paper and chapter number
    Optional<Chapter> findByPaperIdAndChapterNumber(Long paperId, Long chapterNumber);
}
