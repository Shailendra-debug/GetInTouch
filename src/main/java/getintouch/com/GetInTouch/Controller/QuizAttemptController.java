package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitRequestDto;
import getintouch.com.GetInTouch.DTO.Quiz.QuizSubmitResponseDto;
import getintouch.com.GetInTouch.Service.Quiz.QuizAttemptService;
import getintouch.com.GetInTouch.Util.JwtUtil;
import getintouch.com.GetInTouch.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/submit")
    public ResponseEntity<QuizSubmitResponseDto> submit(
            @Valid @RequestBody QuizSubmitRequestDto dto) {
        return ResponseEntity.ok(service.submitQuiz(dto));
    }

    private final QuizAttemptService service;

    @GetMapping("/{attemptId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<QuizSubmitResponseDto> getById(
            @PathVariable Long attemptId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.getByAttemptId(attemptId));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<QuizSubmitResponseDto>> getAllByUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.getAllByUserId(userId));
    }
}

