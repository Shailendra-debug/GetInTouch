package getintouch.com.GetInTouch.Controller.Public;


import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Service.HomePage.Public_Quiz_Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/quiz")
@AllArgsConstructor
@Tag(name = "Public Quiz API", description = "Public APIs for active quizzes")
public class PublicQuizController {
    private final Public_Quiz_Service publicQuizService;

    @GetMapping
    @Operation(
            summary = "Get all active quizzes",
            description = "Returns a list of all active quizzes"
    )
    @ApiResponse(responseCode = "200", description = "Quizzes fetched successfully")
    public List<QuizResponseWithoutQuestionsDTO> getAllActiveQuizzes() {
        return publicQuizService.getAllActiveQuizs();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get active quiz by ID",
            description = "Returns details of an active quiz using its ID"
    )
    @ApiResponse(responseCode = "200", description = "Quiz found")
    @ApiResponse(responseCode = "404", description = "Quiz not found")
    public QuizResponseWithoutQuestionsDTO getActiveQuiz(
            @PathVariable Long id) {
        return publicQuizService.getActiveQuiz(id);
    }

}
