package getintouch.com.GetInTouch.DTO.Quiz;

import lombok.*;

import java.util.List;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionAnswerDto {

    private Long questionId;
    private List<Integer> selectedIndexes;
}
