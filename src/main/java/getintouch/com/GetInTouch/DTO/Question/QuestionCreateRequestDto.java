package getintouch.com.GetInTouch.DTO.Question;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.Option;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCreateRequestDto {

    @NotBlank
    private String question;

    @NotEmpty
    private List<Option> options;

    @NotEmpty
    private Set<Integer> correct;

    private Boolean imageQuestion;
    private String imageUrl;

    private String explanation;

    @NotNull
    @Positive
    private Long chapterId;

    @NotNull
    private QuestionType type;

    // optional → default EASY
    private Difficulty difficulty;

    // optional → default = 1
    private Integer marks;
}


