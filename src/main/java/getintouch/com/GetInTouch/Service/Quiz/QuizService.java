package getintouch.com.GetInTouch.Service.Quiz;

import getintouch.com.GetInTouch.DTO.Quiz.QuizRequestDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;

import java.util.List;

public interface QuizService {

    QuizResponseWithQuestionsDTO createQuiz(QuizRequestDTO request);

    List<QuizResponseWithoutQuestionsDTO> getAllActiveQuizzes();

    QuizResponseWithoutQuestionsDTO getQuizSummary(Long quizId);

    QuizResponseWithQuestionsDTO getQuizWithQuestions(Long quizId);

    QuizResponseWithQuestionsDTO startQuiz(Long quizId);

    QuizResponseWithQuestionsDTO updateQuiz(Long quizId, QuizRequestDTO request);

    void deleteQuiz(Long quizId);
}