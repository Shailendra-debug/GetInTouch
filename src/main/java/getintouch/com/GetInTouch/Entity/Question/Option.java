package getintouch.com.GetInTouch.Entity.Question;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {
    @JsonProperty("img")
    private boolean isImg;
    private String textOrUrl;
    private String url;
}
