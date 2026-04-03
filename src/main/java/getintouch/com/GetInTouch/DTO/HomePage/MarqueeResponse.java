package getintouch.com.GetInTouch.DTO.HomePage;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class MarqueeResponse {
    private String text;
    private boolean active;
    private String url;
}