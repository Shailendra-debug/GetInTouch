package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Quiz.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}