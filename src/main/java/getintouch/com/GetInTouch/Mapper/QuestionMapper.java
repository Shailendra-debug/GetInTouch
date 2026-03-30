package getintouch.com.GetInTouch.Mapper;

import getintouch.com.GetInTouch.DTO.Question.*;
import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class QuestionMapper {

    private final CourseRepository courseRepository;

    /* ---------- CREATE ---------- */

    public Question toEntity(QuestionCreateRequestDto dto) {

        if (dto == null) return null;

        return Question.builder()
                .question(dto.getQuestion())
                .options(dto.getOptions())
                .correct(dto.getCorrect())
                .explanation(dto.getExplanation())
                .type(dto.getType())
                // ✅ default EASY if null
                .difficulty(
                        dto.getDifficulty() != null
                                ? dto.getDifficulty()
                                : Difficulty.EASY
                )
                // ✅ default marks = 1
                .marks(
                        dto.getMarks() != null
                                ? dto.getMarks()
                                : 1
                )
                .build();
    }

    /* ---------- RESPONSE ---------- */
    public static QuestionResponseDto toDto(Question entity) {

        if (entity == null) return null;


        return QuestionResponseDto.builder()
                .id(entity.getId())
                .question(entity.getQuestion())
                .options(entity.getOptions())
                .correct(entity.getCorrect())
                .explanation(entity.getExplanation())
                .type(entity.getType())
                .difficulty(entity.getDifficulty())
                .marks(entity.getMarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /* ---------- UPDATE (PARTIAL) ---------- */
    public void updateEntity(
            QuestionUpdateRequestDto dto,
            Question entity
    ) {
        if (dto == null || entity == null) return;

        if (dto.getQuestion() != null) {
            entity.setQuestion(dto.getQuestion());
        }

        if (dto.getOptions() != null) {
            entity.setOptions(dto.getOptions());
        }

        if (dto.getCorrect() != null) {
            entity.setCorrect(dto.getCorrect());
        }

        if (dto.getExplanation() != null) {
            entity.setExplanation(dto.getExplanation());
        }

        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }

        if (dto.getDifficulty() != null) {
            entity.setDifficulty(dto.getDifficulty());
        }

        if (dto.getMarks() != null) {
            entity.setMarks(dto.getMarks());
        }
    }

    public static QuestionResponseForQuizDTO toQuizView(Question q) {
        return QuestionResponseForQuizDTO.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .options(q.getOptions())
                .type(q.getType())
                .difficulty(q.getDifficulty())
                .marks(q.getMarks())
                .build();
    }

    public static QuestionResponseWithAnswerDTO toAdminView(Question q) {
        return QuestionResponseWithAnswerDTO.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .options(q.getOptions())
                .correct(q.getCorrect())
                .explanation(q.getExplanation())
                .type(q.getType())
                .difficulty(q.getDifficulty())
                .marks(q.getMarks())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .build();
    }
}
