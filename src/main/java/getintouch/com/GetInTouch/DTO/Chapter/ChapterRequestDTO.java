package getintouch.com.GetInTouch.DTO.Chapter;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterRequestDTO {

    private String title;

    @Column(nullable = false)
    private Long chapterNumber;

    private String description;

    private String thumbnail;

    private Long paperId;
}
