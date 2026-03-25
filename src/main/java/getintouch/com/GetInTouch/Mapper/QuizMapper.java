package getintouch.com.GetInTouch.Mapper;



import getintouch.com.GetInTouch.DTO.Question.QuestionResponseForQuizDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Quiz.Quiz;

import java.util.List;

public class QuizMapper {

    private QuizMapper() {
        // utility class
    }

    /* =====================================================
       QUIZ → RESPONSE (WITHOUT QUESTIONS)
       Used for: list / dashboard / admin table
       ===================================================== */
    public static QuizResponseWithoutQuestionsDTO toWithoutQuestions(Quiz quiz) {

        return QuizResponseWithoutQuestionsDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .active(quiz.isActive())
                .type(quiz.getType().name())
                .course(mapCourse(quiz))
                .totalQuestions(
                        quiz.getQuestions() == null ? 0 : quiz.getQuestions().size()
                )
                .passingMarks(quiz.getPassMarks())
                .totalMarks(quiz.getTotalMarks())
                .startTime(quiz.getStartTime())
                .endTime(quiz.getEndTime())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    /* =====================================================
       QUIZ → RESPONSE (WITH QUESTIONS)
       Used for: quiz detail / start quiz
       ===================================================== */
    public static QuizResponseWithQuestionsDTO toWithQuestions(Quiz quiz) {

        List<QuestionResponseForQuizDTO> questionDTOs =
                quiz.getQuestions()
                        .stream()
                        .map(QuizMapper::mapQuestionForQuiz)
                        .toList();

        return QuizResponseWithQuestionsDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .active(quiz.isActive())
                .type(quiz.getType().name())
                .course(mapCourse(quiz))
                .questions(quiz.getQuestions().stream().map(QuestionMapper::toQuizView).toList())
                .totalQuestions(questionDTOs.size())
                .totalMarks(quiz.getTotalMarks())
                .startTime(quiz.getStartTime())
                .passingMarks(quiz.getPassMarks())
                .endTime(quiz.getEndTime())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    /* =====================================================
       HELPERS
       ===================================================== */

    private static CourseResponseDTO mapCourse(Quiz quiz) {
        return CourseResponseDTO.builder()
                .id(quiz.getCourse().getId())
                .name(quiz.getCourse().getName())
                .build();
    }

    private static QuestionResponseForQuizDTO mapQuestionForQuiz(Question q) {
        return QuestionResponseForQuizDTO.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .options(q.getOptions())
                .type(q.getType())
                .difficulty(q.getDifficulty())
                .marks(q.getMarks())
                .build();
    }
}
