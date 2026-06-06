package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {

    // Find paper by id and active status
    Optional<Paper> findByIdAndActiveTrue(Long id);

    // Get all active papers sorted by paperNumber
    List<Paper> findByActiveTrueOrderByPaperNumberAsc();

    // Get all papers of a course sorted by paperNumber
    List<Paper> findByCourseIdOrderByPaperNumberAsc(Long courseId);

    // Get all active papers of a course sorted by paperNumber
    List<Paper> findByCourseIdAndActiveTrueOrderByPaperNumberAsc(Long courseId);

    // Check duplicate paper name within a course
    boolean existsByCourseIdAndNameIgnoreCase(Long courseId, String name);

    // Check duplicate paper number within a course
    boolean existsByCourseIdAndPaperNumber(Long courseId, Long paperNumber);

    // Find by paper number within a course
    Optional<Paper> findByCourseIdAndPaperNumber(Long courseId, Long paperNumber);
}