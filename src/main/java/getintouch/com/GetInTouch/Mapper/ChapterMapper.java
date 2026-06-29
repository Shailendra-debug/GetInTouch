package getintouch.com.GetInTouch.Mapper;


import getintouch.com.GetInTouch.DTO.Chapter.ChapterRequestDTO;
import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;

public final class ChapterMapper {

    private ChapterMapper() {
    }

    /**
     * Convert Request DTO to Entity
     */
    public static Chapter toEntity(
            ChapterRequestDTO dto,
            Paper paper
    ) {

        return Chapter.builder()
                .title(dto.getTitle())
                .chapterNumber(dto.getChapterNumber())
                .description(dto.getDescription())
                .thumbnail(dto.getThumbnail())
                .paper(paper)
                .build();
    }

    /**
     * Convert Entity to Response DTO
     */
    public static ChapterResponseDTO toResponseDTO(
            Chapter chapter,
            Paper paper
    ) {

        return ChapterResponseDTO.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .description(chapter.getDescription())
                .thumbnail(chapter.getThumbnail())
                .active(chapter.getActive())
                .paperId(chapter.getPaper().getId())
                .paperName(paper.getName())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }

    /**
     * Update existing Entity from Request DTO
     */
    public static void updateEntity(
            Chapter chapter,
            ChapterRequestDTO dto
    ) {

        chapter.setTitle(dto.getTitle());
        chapter.setChapterNumber(dto.getChapterNumber());
        chapter.setDescription(dto.getDescription());
        chapter.setThumbnail(dto.getThumbnail());
    }


}