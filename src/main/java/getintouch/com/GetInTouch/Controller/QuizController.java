package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Quiz.QuizRequestDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Service.File.FileUploadService;
import getintouch.com.GetInTouch.Service.Quiz.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Tag(name = "Quiz APIs", description = "Manage quizzes and quiz lifecycle")
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    private final FileUploadService uploadService;

    /* =====================================================
       WRITE OPERATIONS (ADMIN)
       ===================================================== */

    @Operation(summary = "Create Quiz", description = "Create a new quiz with questions (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Quiz created successfully")
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

    @Operation(summary = "Update Quiz", description = "Update quiz details (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Quiz updated successfully")
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

    @Operation(summary = "Delete Quiz", description = "Soft delete a quiz (ADMIN only)")
    @ApiResponse(responseCode = "204", description = "Quiz deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    /* =====================================================
       READ OPERATIONS
       ===================================================== */

    @Operation(summary = "Get All Active Quizzes", description = "Fetch all active quizzes for users")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getAllActiveQuizzes() {
        return ResponseEntity.ok(quizService.getAllActiveQuizzes());
    }

    @Operation(
            summary = "Get All Inactive Quizzes",
            description = "Retrieves a list of all quizzes that are currently inactive. Accessible by administrators only."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/inactive")
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getAllInactiveQuizzes() {
        return ResponseEntity.ok(quizService.getAllInactiveQuizzes());
    }



    @Operation(summary = "Get All Quizzes", description = "Fetch all quizzes (Admin View)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @Operation(summary = "Get Quiz Summary", description = "Fetch quiz details without questions")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{quizId}/summary")
    public ResponseEntity<QuizResponseWithoutQuestionsDTO> getQuizSummary(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getQuizSummary(quizId));
    }

    @Operation(summary = "Get Quiz With Questions", description = "Fetch quiz including all questions (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseWithQuestionsDTO> getQuizWithQuestions(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getQuizWithQuestions(quizId));
    }

    /* =====================================================
       STUDENT TEST TAKING
       ===================================================== */

    @Operation(summary = "Start Quiz", description = "Start quiz, generate attempt, and get questions (USER only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz started successfully"),
            @ApiResponse(responseCode = "400", description = "Quiz has not started or has already ended")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{quizId}/start")
    public ResponseEntity<QuizResponseWithQuestionsDTO> startQuiz(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.startQuiz(quizId));
    }

    /* =====================================================
       ADVANCED FETCHING
       ===================================================== */

    @Operation(summary = "Get All Active Quizzes by Chapter", description = "Fetch all Active quizzes  belonging to a specific chapter")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/active/chapter/{chapterId}")
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getQuizzesByChapter(
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(quizService.getQuizzesByChapter(chapterId));
    }

    @Operation(summary = "Get Currently Running Quizzes", description = "Fetch LIVE or EXAM quizzes currently active based on timestamps")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/running")
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getCurrentlyRunningQuizzes() {
        return ResponseEntity.ok(quizService.getCurrentlyRunningQuizzes());
    }

    /* =====================================================
       ADMIN TRASH BIN
       ===================================================== */

    @Operation(summary = "Get Deactivated Quizzes", description = "View the soft-deleted trash bin for a chapter (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/chapter/{chapterId}/trash")
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getDeactivatedQuizzes(
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(quizService.getDeactivatedQuizzes(chapterId));
    }

    @Operation(summary = "Reactivate Quiz", description = "Restore a soft-deleted quiz from the trash bin (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{quizId}/activate")
    public ResponseEntity<Void> activateQuiz(@PathVariable Long quizId) {
        quizService.activateQuiz(quizId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Deactivate Quiz", description = "Moves an active quiz to the trash bin (ADMIN only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{quizId}/deactivate")
    public ResponseEntity<Void> deactivateQuiz(@PathVariable Long quizId) {
        quizService.deactivateQuiz(quizId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("paperId/{paperId}")
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getGeneralQuizzesByPaper(
            @PathVariable Long paperId) {

        List<QuizResponseWithoutQuestionsDTO> responses = quizService.getGeneralQuizzesByPaper(paperId);

        if (responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Create Thumbnail", description = "Create a new Thumbnail For Quiz (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Thumbnail created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PostMapping("thumbnail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSlider(
            @RequestParam MultipartFile file) {
        String url=uploadService.uploadFile(file,"quiz");
        return ResponseEntity.ok(new HashMap<>().put("URL",url));
    }
}