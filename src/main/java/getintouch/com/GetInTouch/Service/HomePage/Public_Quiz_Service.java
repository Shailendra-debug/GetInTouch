package getintouch.com.GetInTouch.Service.HomePage;

import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Mapper.QuizMapper;
import getintouch.com.GetInTouch.Repository.QuizRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class Public_Quiz_Service {
    private final QuizRepository quizRepository;

    public List<QuizResponseWithoutQuestionsDTO> getAllActiveQuizs(){
        return quizRepository.findByActiveTrue().stream().map(QuizMapper::toWithoutQuestions).toList();
    }

    public QuizResponseWithoutQuestionsDTO getActiveQuiz(Long id){
        return quizRepository.findWithQuestionsById(id)
                .map(QuizMapper::toWithoutQuestions)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }
}
