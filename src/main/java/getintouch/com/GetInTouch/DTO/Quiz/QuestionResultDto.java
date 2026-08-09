package getintouch.com.GetInTouch.DTO.Quiz;


import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.Option;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionResultDto {

    private Long questionId;
    private String question;

    private List<Option> options;

    private List<Integer> selectedIndexes;
    private Set<Integer> correctIndexes;

    private String explanation;

    private Boolean imageQuestion;
    private String imageUrl;
    private Difficulty difficulty;

    private boolean correct;
    private int marksObtained;
}
