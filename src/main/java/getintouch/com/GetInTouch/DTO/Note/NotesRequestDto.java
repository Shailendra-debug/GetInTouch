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

    @NotBlank
    private String title;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    private String description;

    private String thumbnailUrl;

    @NotBlank
    private String pdfUrl;

    private String paymentQrUrl;

    private Boolean active = true;
}