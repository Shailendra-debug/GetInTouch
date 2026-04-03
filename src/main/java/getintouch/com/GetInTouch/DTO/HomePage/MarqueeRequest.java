package getintouch.com.GetInTouch.DTO.HomePage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarqueeRequest {
    private String text;
    private boolean active;
    private String url;

    // getters & setters
}
