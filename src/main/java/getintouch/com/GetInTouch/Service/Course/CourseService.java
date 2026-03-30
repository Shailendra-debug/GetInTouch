package getintouch.com.GetInTouch.Service.Course;



import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;

import java.util.List;

public interface CourseService {

    CourseResponseDTO createCourse(CourseRequestDTO request);

    List<CourseResponseDTO> getAllCourses();

    CourseResponseDTO getCourseById(Long courseId);

    CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO request);

    void deleteCourse(Long courseId);
}
