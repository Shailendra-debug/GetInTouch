package getintouch.com.GetInTouch.DTO.Paper;

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
public class PaperResponseDTO {

    private Long id;

    private String name;

    @Column(nullable = false)
    private Long paperNumber;

    private String description;

    private String thumbnail;

    private Long courseId;

    private String courseName;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
