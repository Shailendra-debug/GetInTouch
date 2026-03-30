package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Question.QuestionCreateRequestDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionUpdateRequestDto;
import getintouch.com.GetInTouch.Service.Question.QuestionService;
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

@Tag(name = "Question APIs", description = "Manage quiz/interview questions")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /* ---------- CREATE ---------- */
    @Operation(summary = "Create Question", description = "Create a new question (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Question created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponseDto> create(
            @Valid @RequestBody QuestionCreateRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(questionService.create(request));
    }

    /* ---------- READ BY ID ---------- */
    @Operation(summary = "Get Question By ID", description = "Fetch question by ID")
    @ApiResponse(responseCode = "200", description = "Question found")
    @ApiResponse(responseCode = "404", description = "Question not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<QuestionResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity.ok(questionService.getById(id));
    }

    /* ---------- READ ALL ---------- */
    @Operation(summary = "Get All Questions", description = "Fetch all questions")
    @ApiResponse(responseCode = "200", description = "Questions fetched successfully")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<QuestionResponseDto>> getAll() {

        return ResponseEntity.ok(questionService.getAll());
    }

    /* ---------- UPDATE ---------- */
    @Operation(summary = "Update Question", description = "Update existing question (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Question updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody QuestionUpdateRequestDto request) {

        return ResponseEntity.ok(questionService.update(id, request));
    }

    /* ---------- DELETE ---------- */
    @Operation(summary = "Delete Question", description = "Delete a question (ADMIN only)")
    @ApiResponse(responseCode = "204", description = "Question deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}