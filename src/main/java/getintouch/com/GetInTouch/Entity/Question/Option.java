package getintouch.com.GetInTouch.Entity.Question;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {
    private boolean isImg;
    private String textOrUrl;
}
