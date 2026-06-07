package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Question.QuestionCreateRequestDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionUpdateRequestDto;
import getintouch.com.GetInTouch.Entity.Quiz.Course;
import getintouch.com.GetInTouch.Service.Question.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question Management", description = "Endpoints for creating, managing, and randomly generating quiz questions")
public class QuestionController {

    private final QuestionService questionService;

    // =========================================================================
    // WRITE OPERATIONS (Admins Only)
    // =========================================================================

    @Operation(summary = "Create a question", description = "Adds a new question to a specific active chapter.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Question created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error (e.g., empty options, out-of-bounds correct index)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<QuestionResponseDto> createQuestion(@RequestBody QuestionCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(request));
    }

    @Operation(summary = "Update a question", description = "Modifies question text, options, correct answers, or moves it to a new chapter.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> updateQuestion(@PathVariable Long id, @RequestBody QuestionUpdateRequestDto request) {
        return ResponseEntity.ok(questionService.update(id, request));
    }

    @Operation(summary = "Soft delete a question", description = "Deactivates a question so it no longer appears in quizzes.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // READ OPERATIONS (Users & Admins)
    // =========================================================================

    @Operation(summary = "Get all questions", description = "Retrieves all active questions across all chapters.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<QuestionResponseDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAll());
    }

    @Operation(summary = "Get question by ID", description = "Fetches a single active question completely with its options and answers.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDto> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getById(id));
    }

    // =========================================================================
    // ADVANCED FETCHING (Users & Admins)
    // =========================================================================

    @Operation(summary = "Get questions by Chapter", description = "Retrieves all active questions for a specific chapter.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByChapter(@PathVariable Long chapterId) {
        return ResponseEntity.ok(questionService.getByChapterId(chapterId));
    }

    @Operation(summary = "Get questions by Multiple Chapters", description = "Pass a comma-separated list of chapter IDs to fetch bulk questions.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/course")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionsByMultipleChapters(
            @Parameter(description = "List of chapter IDs, e.g., 1,2,3") @RequestParam Long paperId) {
        return ResponseEntity.ok(questionService.getByMultipleChapters(paperId));
    }

    @Operation(summary = "Search questions", description = "Searches question text across the database.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<QuestionResponseDto>> searchQuestions(@RequestParam String keyword) {
        return ResponseEntity.ok(questionService.searchByKeyword(keyword));
    }

    @Operation(summary = "Generate Random Quiz", description = "Highly optimized endpoint to grab a random subset of questions for a test.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/random")
    public ResponseEntity<List<QuestionResponseDto>> getRandomQuiz(
            @Parameter(description = "The target Chapter ID") @RequestParam Long chapterId,
            @Parameter(description = "Number of questions to return (defaults to 10)") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(questionService.generateRandomQuiz(chapterId, limit));
    }

    @Operation(summary = "Count questions in Chapter", description = "Returns the total number of active questions in a given chapter.")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/chapter/{chapterId}/count")
    public ResponseEntity<Long> getChapterQuestionCount(@PathVariable Long chapterId) {
        return ResponseEntity.ok(questionService.countByChapter(chapterId));
    }

    // =========================================================================
    // ADMIN / TRASH BIN MANAGEMENT (Admins Only)
    // =========================================================================

    @Operation(summary = "View Trash Bin by Chapter", description = "Fetches all deactivated (soft-deleted) questions for a chapter.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/chapter/{chapterId}/trash")
    public ResponseEntity<List<QuestionResponseDto>> getDeactivatedQuestions(@PathVariable Long chapterId) {
        return ResponseEntity.ok(questionService.getDeactivatedQuestionsByChapter(chapterId));
    }

    @Operation(summary = "Reactivate Question", description = "Restores a soft-deleted question.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateQuestion(@PathVariable Long id) {
        questionService.activate(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Permanently Delete Question", description = "DANGER: Hard deletes the question from the database forever.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteQuestion(@PathVariable Long id) {
        questionService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}