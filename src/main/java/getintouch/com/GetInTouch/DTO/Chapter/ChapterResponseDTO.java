package getintouch.com.GetInTouch.DTO.Chapter;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterResponseDTO {

    private Long id;

    private String title;

    @Column(nullable = false)
    private Long chapterNumber;

    private String description;

    private String thumbnail;

    private Boolean active;

    private Long paperId;

    private String paperName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
