package getintouch.com.GetInTouch.DTO.Question;


import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.Option;
import getintouch.com.GetInTouch.Entity.Question.QuestionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDto {

    private Long id;
    private String question;
    private List<Option> options;
    private Set<Integer> correct;
    private Boolean imageQuestion;
    private String imageUrl;
    private String explanation;
    private QuestionType type;
    private Difficulty difficulty;
    private Integer marks;
    private Boolean active;
    private String chapterName;
    private Long chapterId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
