package getintouch.com.GetInTouch.DTO.HomePage;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SliderResponse {
    private Long id;
    private String imageUrl;
    private boolean active;
}