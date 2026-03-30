package getintouch.com.GetInTouch.DTO.Quiz;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizSubmitRequestDto {

    @NotNull
    private Long attemptId;//Quiz AttemptId


    private List<QuestionAnswerDto> answers;
}