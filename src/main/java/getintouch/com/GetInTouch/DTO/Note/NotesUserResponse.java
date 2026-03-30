package getintouch.com.GetInTouch.DTO.Note;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotesUserResponse {

    private Long id;
    private String title;
    private Double price;
    private String thumbnailUrl;
    private Boolean approved;
}