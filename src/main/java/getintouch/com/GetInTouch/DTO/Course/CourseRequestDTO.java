package getintouch.com.GetInTouch.DTO.Course;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {

    private String name;

    @Column(nullable = false)
    private Long courseNumber;

    private String description;

    private String thumbnail;


}