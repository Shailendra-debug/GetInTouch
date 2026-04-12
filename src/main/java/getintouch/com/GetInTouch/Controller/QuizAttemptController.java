package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitByQuizIdDtu;
import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitRequestDto;
import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitResponseDto;
import getintouch.com.GetInTouch.Service.Quiz.QuizAttemptService;
import getintouch.com.GetInTouch.security.SecurityUtil;
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
import java.util.Map;

@Tag(name = "Quiz Attempt APIs", description = "Submit quiz and view results")
@RestController
@RequestMapping("/api/quiz-attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService service;

    /* =====================================================
       SUBMIT QUIZ (USER)
       ===================================================== */
    @Operation(summary = "Submit Quiz", description = "Submit quiz answers and get result (USER only)")
    @ApiResponse(responseCode = "200", description = "Quiz submitted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only USER allowed")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/submit")
    public ResponseEntity<QuizSubmitResponseDto> submit(
            @Valid @RequestBody QuizSubmitRequestDto dto) {

        return ResponseEntity.ok(service.submitQuiz(dto));
    }

    /* =====================================================
       GET ATTEMPT BY ID
       ===================================================== */
    @Operation(summary = "Get Quiz Attempt By ID", description = "Fetch quiz attempt details by ID")
    @ApiResponse(responseCode = "200", description = "Attempt found")
    @ApiResponse(responseCode = "404", description = "Attempt not found")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{attemptId}")
    public ResponseEntity<QuizSubmitResponseDto> getById(
            @PathVariable Long attemptId) {

        return ResponseEntity.ok(service.getByAttemptId(attemptId));
    }

    /* =====================================================
       GET ALL ATTEMPTS OF CURRENT USER
       ===================================================== */
    @Operation(summary = "Get User Quiz Attempts", description = "Fetch all quiz attempts of logged-in user")
    @ApiResponse(responseCode = "200", description = "Attempts fetched successfully")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<QuizSubmitResponseDto>> getAllByUser() {

        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.getAllByUserId(userId));
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/quiz/{id}")
    public ResponseEntity<List<QuizSubmitByQuizIdDtu>>getAllSubmitQuizForAdmin(@PathVariable long id){
        List<QuizSubmitByQuizIdDtu>list=service.getAllByQuizId(id);
        if (list==null)return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(list);
    }
}