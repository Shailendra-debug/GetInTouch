package getintouch.com.GetInTouch.DTO.Note;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotesRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    private String description;

    private String thumbnailUrl;

    @NotBlank(message = "PDF URL is required")
    private String pdfUrl;

    @NotNull(message = "Paper ID is required")
    private Long paperId;

    @Builder.Default
    private Boolean active = true;
}