package getintouch.com.GetInTouch.DTO.Course;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {

    @NotBlank
    private String name;
}