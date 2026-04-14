package getintouch.com.GetInTouch.DTO.HomePage;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactUsDTO {
    private String subject;
    private String message;
}
