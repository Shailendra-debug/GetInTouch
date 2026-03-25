package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Quiz.QuizRequestDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Service.Quiz.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quiz APIs", description = "Manage quizzes and quiz lifecycle")
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /* =====================================================
       CREATE QUIZ (ADMIN)
       ===================================================== */
    @Operation(summary = "Create Quiz", description = "Create a new quiz with questions (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Quiz created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<QuizResponseWithQuestionsDTO> createQuiz(
            @Valid @RequestBody QuizRequestDTO request
    ) {
        return new ResponseEntity<>(
                quizService.createQuiz(request),
                HttpStatus.CREATED
        );
    }

    /* =====================================================
       GET ALL QUIZZES (USER + ADMIN)
       ===================================================== */
    @Operation(summary = "Get All Quizzes", description = "Fetch all active quizzes")
    @ApiResponse(responseCode = "200", description = "Quizzes fetched successfully")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllActiveQuizzes());
    }

    /* =====================================================
       GET QUIZ SUMMARY
       ===================================================== */
    @Operation(summary = "Get Quiz Summary", description = "Fetch quiz details without questions")
    @ApiResponse(responseCode = "200", description = "Quiz summary fetched")
    @ApiResponse(responseCode = "404", description = "Quiz not found")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{quizId}/summary")
    public ResponseEntity<QuizResponseWithoutQuestionsDTO> getQuizSummary(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getQuizSummary(quizId));
    }

    /* =====================================================
       GET QUIZ WITH QUESTIONS (ADMIN)
       ===================================================== */
    @Operation(summary = "Get Quiz With Questions", description = "Fetch quiz including all questions (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Quiz fetched successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseWithQuestionsDTO> getQuizWithQuestions(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getQuizWithQuestions(quizId));
    }

    /* =====================================================
       START QUIZ (USER)
       ===================================================== */
    @Operation(summary = "Start Quiz", description = "Start quiz and get questions (USER only)")
    @ApiResponse(responseCode = "200", description = "Quiz started successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only USER allowed")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{quizId}/start")
    public ResponseEntity<QuizResponseWithQuestionsDTO> startQuiz(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.startQuiz(quizId));
    }

    /* =====================================================
       UPDATE QUIZ (ADMIN)
       ===================================================== */
    @Operation(summary = "Update Quiz", description = "Update quiz details (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Quiz updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{quizId}")
    public ResponseEntity<QuizResponseWithQuestionsDTO> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequestDTO request
    ) {
        return ResponseEntity.ok(
                quizService.updateQuiz(quizId, request)
        );
    }

    /* =====================================================
       DELETE QUIZ (ADMIN)
       ===================================================== */
    @Operation(summary = "Delete Quiz", description = "Delete a quiz (ADMIN only)")
    @ApiResponse(responseCode = "204", description = "Quiz deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
    }
}