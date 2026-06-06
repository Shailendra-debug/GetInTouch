package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Quiz.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find active course by id
    Optional<Course> findByIdAndActiveTrue(Long id);

    // Get all active courses sorted by courseNumber
    List<Course> findByActiveTrueOrderByCourseNumberAsc();

    // Get all courses sorted by courseNumber
    List<Course> findAllByOrderByCourseNumberAsc();

    // Check duplicate course name
    boolean existsByNameIgnoreCase(String name);

    // Check duplicate course number
    boolean existsByCourseNumber(Long courseNumber);

    // Find course by course number
    Optional<Course> findByCourseNumber(Long courseNumber);

    // Find active course by course number
    Optional<Course> findByCourseNumberAndActiveTrue(Long courseNumber);
}