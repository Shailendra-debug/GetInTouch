package getintouch.com.GetInTouch.DTO.Paper;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false)
    private Long paperNumber;

    private String description;

    private String thumbnail;

    @NotNull(message = "Course id is required")
    private Long courseId;
}
