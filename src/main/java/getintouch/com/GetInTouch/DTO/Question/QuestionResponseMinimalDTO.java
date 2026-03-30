package getintouch.com.GetInTouch.DTO.Question;

import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseMinimalDTO {

    private Long id;
    private String question;
    private QuestionType type;
    private Difficulty difficulty;
    private Integer marks;
}
