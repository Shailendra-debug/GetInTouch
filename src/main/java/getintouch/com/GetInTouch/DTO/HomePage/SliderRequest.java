package getintouch.com.GetInTouch.DTO.HomePage;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SliderRequest {
    private String imageUrl;
    private boolean active;
    private Integer displayOrder;

    // getters & setters
}
