package getintouch.com.GetInTouch.DTO.Question;


import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDto {

    private Long id;
    private String question;
    private List<String> options;
    private List<Integer> correct;
    private String explanation;
    private QuestionType type;
    private Difficulty difficulty;
    private Integer marks;
    private String courseName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
