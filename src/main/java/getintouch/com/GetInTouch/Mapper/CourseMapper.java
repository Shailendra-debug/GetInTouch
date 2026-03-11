package getintouch.com.GetInTouch.Mapper;


import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Course;

public class CourseMapper {

    private CourseMapper() {
        // Utility class – prevent instantiation
    }

    /* =====================================================
       REQUEST DTO → ENTITY
       ===================================================== */
    public static Course toEntity(CourseRequestDTO dto) {

        return Course.builder()
                .name(dto.getName())
                .build();
    }

    /* =====================================================
       ENTITY → RESPONSE DTO
       ===================================================== */
    public static CourseResponseDTO toResponse(Course course) {

        return CourseResponseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .build();
    }
}
