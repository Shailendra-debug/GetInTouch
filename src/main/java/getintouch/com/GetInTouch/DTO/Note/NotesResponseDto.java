package getintouch.com.GetInTouch.DTO.Note;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotesResponseDto {

    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String thumbnailUrl;
    private String pdfUrl;
    private String paymentQrUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}
