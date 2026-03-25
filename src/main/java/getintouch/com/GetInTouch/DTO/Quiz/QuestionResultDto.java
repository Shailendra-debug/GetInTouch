package getintouch.com.GetInTouch.DTO.Quiz;


import lombok.*;

import java.util.List;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionResultDto {

    private Long questionId;
    private List<Integer> selectedIndexes;
    private List<Integer> correctIndexes;
    private boolean correct;
    private int marksObtained;
}
