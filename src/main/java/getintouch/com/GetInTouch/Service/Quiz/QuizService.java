package getintouch.com.GetInTouch.Service.Quiz;

import getintouch.com.GetInTouch.DTO.Quiz.QuizRequestDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;

import java.util.List;

public interface QuizService {

    // =========================================================================
    // CORE WRITE OPERATIONS
    // =========================================================================

    QuizResponseWithQuestionsDTO createQuiz(QuizRequestDTO request);

    QuizResponseWithQuestionsDTO updateQuiz(Long quizId, QuizRequestDTO request);

    void deleteQuiz(Long quizId);

    // =========================================================================
    // CORE READ OPERATIONS
    // =========================================================================

    List<QuizResponseWithoutQuestionsDTO> getAllQuizzes();

    /**
     * Identical to getAllQuizzes due to @SQLRestriction, kept for backward compatibility.
     */
    List<QuizResponseWithoutQuestionsDTO> getAllActiveQuizzes();

    QuizResponseWithoutQuestionsDTO getQuizSummary(Long quizId);

    List<QuizResponseWithoutQuestionsDTO> getGeneralQuizzesByPaper(Long paperId);

    /**
     * For Admins/Teachers to view the full quiz setup.
     */
    QuizResponseWithQuestionsDTO getQuizWithQuestions(Long quizId);

    /**
     * For Students taking the test. Includes schedule validation for LIVE/EXAM quizzes.
     */
    QuizResponseWithQuestionsDTO startQuiz(Long quizId);

    // =========================================================================
    // ADVANCED FETCHING
    // =========================================================================

    List<QuizResponseWithoutQuestionsDTO> getQuizzesByChapter(Long chapterId);

    List<QuizResponseWithoutQuestionsDTO> getCurrentlyRunningQuizzes();

    // =========================================================================
    // ADMIN TRASH BIN
    // =========================================================================

    List<QuizResponseWithoutQuestionsDTO> getDeactivatedQuizzes(Long chapterId);

    void activateQuiz(Long quizId);
}