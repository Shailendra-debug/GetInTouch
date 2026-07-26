package getintouch.com.GetInTouch.Mapper;

import getintouch.com.GetInTouch.DTO.Quiz.QuestionResultDto;
import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitResponseDto;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttempt;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttemptAnswer;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
@Component
public class
QuizAttemptMapper {
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

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
                .startTime(
                        attempt.getStartTime() != null
                                ? attempt.getStartTime().atZoneSameInstant(IST)
                                : null
                )
                .endTime(
                        attempt.getEndTime() != null
                                ? attempt.getEndTime().atZoneSameInstant(IST)
                                : null
                )
                .results(results)
                .showResult(attempt.getQuiz().getShowResult())
                .build();
    }

    /* ---------- ANSWER ENTITY → RESULT DTO ---------- */
    public QuestionResultDto toQuestionResult(
            QuizAttemptAnswer answer
    ) {
        return QuestionResultDto.builder()
                .questionId(answer.getQuestion().getId())

                // ✅ Added (VERY IMPORTANT)
                .question(answer.getQuestion().getQuestion())
                .options(answer.getQuestion().getOptions())
                .explanation(answer.getQuestion().getExplanation())
                .imageQuestion(answer.getQuestion().isImageQuestion())
                .imageUrl(answer.getQuestion().getImageUrl())
                .difficulty(answer.getQuestion().getDifficulty())

                .selectedIndexes(
                        answer.getSelectedIndexes() != null
                                ? answer.getSelectedIndexes()
                                : List.of()
                )

                // ✅ FIXED (no duplication)
                .correctIndexes(answer.getQuestion().getCorrect())

                .correct(answer.isCorrect())
                .marksObtained(answer.getMarksObtained())
                .build();
    }
}