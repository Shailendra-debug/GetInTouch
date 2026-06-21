package getintouch.com.GetInTouch.Mapper;

import getintouch.com.GetInTouch.DTO.Question.*;
import getintouch.com.GetInTouch.Entity.Question.Difficulty;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    /* ---------- CREATE ---------- */
    public Question toEntity(QuestionCreateRequestDto dto) {

        if (dto == null) {
            return null;
        }

        return Question.builder()
                .question(dto.getQuestion())
                .options(dto.getOptions())
                .correct(dto.getCorrect())
                .imageQuestion(dto.getImageQuestion())
                .imageUrl(dto.getImageUrl())
                .explanation(dto.getExplanation())
                .type(dto.getType())
                .difficulty(
                        dto.getDifficulty() != null
                                ? dto.getDifficulty()
                                : Difficulty.EASY
                )
                .marks(
                        dto.getMarks() != null
                                ? dto.getMarks()
                                : 1
                )
                .active(true)
                .build();
    }

    /* ---------- RESPONSE ---------- */
    public QuestionResponseDto toDto(Question entity) {

        if (entity == null) {
            return null;
        }

        return QuestionResponseDto.builder()
                .id(entity.getId())
                .question(entity.getQuestion())
                .options(entity.getOptions())
                .correct(entity.getCorrect())
                .imageQuestion(entity.isImageQuestion())
                .imageUrl(entity.getImageUrl())
                .explanation(entity.getExplanation())
                .type(entity.getType())
                .difficulty(entity.getDifficulty())
                .marks(entity.getMarks())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /* ---------- UPDATE ---------- */
    public void updateEntity(
            QuestionUpdateRequestDto dto,
            Question entity
    ) {

        if (dto == null || entity == null) {
            return;
        }

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

        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }

        if (dto.getImageQuestion() != null) {
            entity.setImageQuestion(dto.getImageQuestion());
        }
    }

    /* ---------- QUIZ VIEW ---------- */
    public QuestionResponseForQuizDTO toQuizView(Question question) {

        if (question == null) {
            return null;
        }

        return QuestionResponseForQuizDTO.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .options(question.getOptions())
                .type(question.getType())
                .difficulty(question.getDifficulty())
                .marks(question.getMarks())
                .build();
    }

    /* ---------- ADMIN VIEW ---------- */
    public QuestionResponseWithAnswerDTO toAdminView(Question question) {

        if (question == null) {
            return null;
        }

        return QuestionResponseWithAnswerDTO.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .options(question.getOptions())
                .correct(question.getCorrect())
                .explanation(question.getExplanation())
                .type(question.getType())
                .difficulty(question.getDifficulty())
                .marks(question.getMarks())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}