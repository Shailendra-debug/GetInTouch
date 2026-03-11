package getintouch.com.GetInTouch.DTO.Question;


import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseForQuizDTO {

    private Long id;
    private String question;
    private List<String> options;
    private QuestionType type;
    private Difficulty difficulty;
    private Integer marks;
}

