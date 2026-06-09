package getintouch.com.GetInTouch.DTO.Note;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotesResponseForUserDto {

    private Long id;

    private String title;

    private BigDecimal price;

    private String description;

    private String thumbnailUrl;

    private String pdfUrl;

    private Long paperId;

    private String paperName;

    private Boolean isPurchase;

    private LocalDateTime createdAt;


}
