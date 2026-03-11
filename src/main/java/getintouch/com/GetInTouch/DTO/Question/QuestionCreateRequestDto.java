package getintouch.com.GetInTouch.DTO.Question;

import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCreateRequestDto {

    @NotBlank
    private String question;

    @NotEmpty
    private List<String> options;

    @NotEmpty
    private List<Integer> correct;

    private String explanation;

    @NotNull
    @Positive
    private Long courseId;

    @NotNull
    private QuestionType type;

    // optional → default EASY
    private Difficulty difficulty;

    // optional → default = 1
    private Integer marks;
}


