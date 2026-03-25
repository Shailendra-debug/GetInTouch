package getintouch.com.GetInTouch.Mapper;

import getintouch.com.GetInTouch.DTO.Quiz.QuestionResultDto;
import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitResponseDto;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttempt;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttemptAnswer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizAttemptMapper {

    /* ---------- ENTITY → RESPONSE DTO ---------- */
    public QuizSubmitResponseDto toResponse(
            QuizAttempt attempt,
            List<QuestionResultDto> results
    ) {

        Long userId = attempt.getUser() != null
                ? attempt.getUser().getId()
                : null;

        return QuizSubmitResponseDto.builder()
                .attemptId(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .userId(userId)
                .totalQuestions(attempt.getTotalQuestions())
                .attemptedQuestions(attempt.getAttemptedQuestions())
                .correctAnswers(attempt.getCorrectAnswers())
                .wrongAnswers(attempt.getWrongAnswers())
                .score(attempt.getScore())
                .totalMarks(attempt.getTotalMarks())
                .percentage(attempt.getPercentage())
                .status(attempt.getStatus().name())
                .startTime(attempt.getStartTime())
                .endTime(attempt.getEndTime())
                .results(results)
                .build();
    }

    /* ---------- ANSWER ENTITY → RESULT DTO ---------- */
    public QuestionResultDto toQuestionResult(
            QuizAttemptAnswer answer
    ) {
        return QuestionResultDto.builder()
                .questionId(answer.getQuestion().getId())
                .selectedIndexes(answer.getSelectedIndexes())
                .correctIndexes(answer.getCorrectIndexes())
                .correct(answer.isCorrect())
                .marksObtained(answer.getMarksObtained())
                .build();
    }
}
