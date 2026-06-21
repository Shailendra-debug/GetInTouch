package getintouch.com.GetInTouch.DTO.Quiz;


import getintouch.com.GetInTouch.Entity.Question.Option;
import lombok.*;

import java.util.List;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionResultDto {

    private Long questionId;
    private String question;

    private List<Option> options;

    private List<Integer> selectedIndexes;
    private List<Integer> correctIndexes;

    private String explanation;

    private boolean correct;
    private int marksObtained;
}
