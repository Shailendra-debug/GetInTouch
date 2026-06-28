package getintouch.com.GetInTouch.Service.Course;



import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;

import java.util.List;

public interface CourseService {

    // ========== ADMIN ==========

    CourseResponseDTO createCourse(
            CourseRequestDTO requestDTO
    );

    CourseResponseDTO updateCourse(
            Long id,
            CourseRequestDTO requestDTO
    );

    void deleteCourse(Long id);

    CourseResponseDTO activateCourse(Long id);

    CourseResponseDTO deactivateCourse(Long id);

    List<CourseResponseDTO> getAllCourses();

    // ========== USER ==========

    List<CourseResponseDTO> getAllActiveCourses();

    CourseResponseDTO getCourseById(Long id);

    CourseResponseDTO getCourseByCourseNumber(
            Long courseNumber
    );

    List<CourseResponseDTO> searchCourses(
            String keyword
    );
}
