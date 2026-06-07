package getintouch.com.GetInTouch.Controller.Public;

import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Service.Quiz.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/quiz")
@RequiredArgsConstructor
@Tag(name = "Public Quiz API", description = "Public APIs for active quizzes (No authentication required)")
public class PublicQuizController {

    // Injecting the main optimized QuizService instead of the redundant Public_Quiz_Service
    private final QuizService quizService;

    @GetMapping
    @Operation(
            summary = "Get all active quizzes",
            description = "Returns a list of all active quizzes"
    )
    @ApiResponse(responseCode = "200", description = "Quizzes fetched successfully")
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getAllActiveQuizzes() {
        return ResponseEntity.ok(quizService.getAllActiveQuizzes());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get active quiz by ID",
            description = "Returns details of an active quiz using its ID without exposing questions"
    )
    @ApiResponse(responseCode = "200", description = "Quiz found")
    @ApiResponse(responseCode = "404", description = "Quiz not found")
    public ResponseEntity<QuizResponseWithoutQuestionsDTO> getActiveQuiz(
            @PathVariable Long id) {
        // We use getQuizSummary here to prevent N+1 queries and avoid sending questions to the public
        return ResponseEntity.ok(quizService.getQuizSummary(id));
    }
}