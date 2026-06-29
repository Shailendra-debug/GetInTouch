package getintouch.com.GetInTouch.Service.Question;



import getintouch.com.GetInTouch.DTO.Question.QuestionCreateRequestDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionUpdateRequestDto;

import java.util.List;

public interface QuestionService {

    // =========================================================================
    // CORE CRUD
    // =========================================================================

    QuestionResponseDto create(QuestionCreateRequestDto request);

    List<QuestionResponseDto> createListOfQus(List<QuestionCreateRequestDto> request);



    QuestionResponseDto getById(Long id);

    List<QuestionResponseDto> getAll();

    List<QuestionResponseDto> getAllActiveQuestions();

    List<QuestionResponseDto> getAllInactiveQuestions();

    QuestionResponseDto update(Long id, QuestionUpdateRequestDto request);

    /**
     * Soft deletes the question (sets active = false).
     */
    void delete(Long id);

    // =========================================================================
    // ADVANCED FETCHING
    // =========================================================================

    List<QuestionResponseDto> getByChapterId(Long chapterId);

    List<QuestionResponseDto> getByMultipleChapters(Long chapterIds);

    List<QuestionResponseDto> searchByKeyword(String keyword);

    List<QuestionResponseDto> generateRandomQuiz(Long chapterId, int limit);

    long countByChapter(Long chapterId);

    // =========================================================================
    // ADMIN / TRASH BIN MANAGEMENT
    // =========================================================================

    List<QuestionResponseDto> getDeactivatedQuestionsByChapter(Long chapterId);

    void activate(Long id);

    void deactivate(Long id);

    void hardDelete(Long id);
}