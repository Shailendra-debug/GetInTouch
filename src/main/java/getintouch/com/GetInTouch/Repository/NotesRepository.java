package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Note.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {

    // ----------------------------------------------------
    // Fixed with @EntityGraph for derived query methods
    // ----------------------------------------------------

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByActiveTrue();

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    Optional<Notes> findByIdAndActiveTrue(Long id);

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByTitleContainingIgnoreCase(String keyword);

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByPriceLessThanEqual(BigDecimal price);

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByPaperId(Long paperId);

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByPaperIdAndActiveTrue(Long paperId);

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByPaperCourseId(Long courseId);

    @EntityGraph(attributePaths = {"paper", "paper.course"})
    List<Notes> findByPaperCourseIdAndActiveTrue(Long courseId);

    // ----------------------------------------------------
    // Fixed with JOIN FETCH for custom JPQL queries
    // ----------------------------------------------------

    @Query("""
           SELECT n
           FROM Notes n
           JOIN FETCH n.paper p
           JOIN FETCH p.course c
           WHERE n.active = true
           ORDER BY n.createdAt DESC
           """)
    List<Notes> findLatestActiveNotes();

}