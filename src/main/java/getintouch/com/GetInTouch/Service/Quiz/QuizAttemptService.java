package getintouch.com.GetInTouch.Service.Quiz;

import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitByQuizIdDtu;
import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitRequestDto;
import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitResponseDto;

import java.util.List;

public interface QuizAttemptService {

    QuizSubmitResponseDto submitQuiz(QuizSubmitRequestDto request);

    QuizSubmitResponseDto getByAttemptId(Long attemptId);

    List<QuizSubmitResponseDto> getAllByUserId(Long userId);

    List<QuizSubmitByQuizIdDtu> getAllByQuizId(Long quizId);

}

