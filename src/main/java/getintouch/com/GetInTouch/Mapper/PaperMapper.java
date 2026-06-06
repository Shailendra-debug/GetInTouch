package getintouch.com.GetInTouch.Mapper;


import getintouch.com.GetInTouch.DTO.Paper.PaperRequestDTO;
import getintouch.com.GetInTouch.DTO.Paper.PaperResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Course;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;

public final class PaperMapper {

    private PaperMapper() {
    }

    public static Paper toEntity(
            PaperRequestDTO dto,
            Course course
    ) {

        return Paper.builder()
                .name(dto.getName())
                .paperNumber(dto.getPaperNumber())
                .description(dto.getDescription())
                .thumbnail(dto.getThumbnail())
                .course(course)
                .build();
    }

    public static PaperResponseDTO toResponseDTO(
            Paper paper,
            Course course
    ) {

        return PaperResponseDTO.builder()
                .id(paper.getId())
                .name(paper.getName())
                .paperNumber(paper.getPaperNumber())
                .description(paper.getDescription())
                .thumbnail(paper.getThumbnail())
                .active(paper.getActive())
                .courseId(paper.getCourse().getId())
                .courseName(course.getName())
                .createdAt(paper.getCreatedAt())
                .updatedAt(paper.getUpdatedAt())
                .build();
    }

    public static void updateEntity(
            Paper paper,
            PaperRequestDTO dto
    ) {

        paper.setName(dto.getName());
        paper.setPaperNumber(dto.getPaperNumber());
        paper.setDescription(dto.getDescription());
        paper.setThumbnail(dto.getThumbnail());
    }
}
