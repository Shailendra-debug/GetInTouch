package getintouch.com.GetInTouch.Mapper;


import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Course;

public final class CourseMapper {

    private CourseMapper() {
    }

    /**
     * Convert Request DTO to Entity
     */
    public static Course toEntity(CourseRequestDTO dto) {

        return Course.builder()
                .name(dto.getName())
                .courseNumber(dto.getCourseNumber())
                .description(dto.getDescription())
                .thumbnail(dto.getThumbnail())
                .build();
    }

    /**
     * Convert Entity to Response DTO
     */
    public static CourseResponseDTO toResponseDTO(Course course) {

        return CourseResponseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .courseNumber(course.getCourseNumber())
                .description(course.getDescription())
                .thumbnail(course.getThumbnail())
                .active(course.getActive())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    /**
     * Update existing Entity from Request DTO
     */
    public static void updateEntity(
            Course course,
            CourseRequestDTO dto
    ) {

        course.setName(dto.getName());
        course.setCourseNumber(dto.getCourseNumber());
        course.setDescription(dto.getDescription());
        course.setThumbnail(dto.getThumbnail());
    }
}
