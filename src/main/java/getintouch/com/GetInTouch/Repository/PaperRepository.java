package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {

    // 1. Fixed Duplication: Kept the version with the EntityGraph
    @EntityGraph(attributePaths = "course")
    Optional<Paper> findByIdAndActiveTrue(Long id);

    @EntityGraph(attributePaths = "course")
    List<Paper> findAll();

    @EntityGraph(attributePaths = "course")
    List<Paper> findByActiveTrueOrderByPaperNumberAsc();

    @EntityGraph(attributePaths = "course")
    List<Paper> findByCourseIdOrderByPaperNumberAsc(Long courseId);

    @EntityGraph(attributePaths = "course")
    List<Paper> findByCourseIdAndActiveTrueOrderByPaperNumberAsc(Long courseId);

    @EntityGraph(attributePaths = "course")
    Optional<Paper> findByCourseIdAndPaperNumber(Long courseId, Long paperNumber);

    // 2. Added to completely fix the search bottleneck on the database layer
    @EntityGraph(attributePaths = "course")
    List<Paper> findByActiveTrueAndNameContainingIgnoreCaseOrderByPaperNumberAsc(String keyword);

    // --- Validation Queries ---
    boolean existsByCourseIdAndNameIgnoreCase(Long courseId, String name);

    boolean existsByCourseIdAndPaperNumber(Long courseId, Long paperNumber);
}