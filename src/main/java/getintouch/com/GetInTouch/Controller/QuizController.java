package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Quiz.QuizRequestDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Service.Quiz.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /* =====================================================
       CREATE QUIZ (ADMIN)
       ===================================================== */
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
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<QuizResponseWithoutQuestionsDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllActiveQuizzes());
    }

    /* =====================================================
       GET QUIZ SUMMARY (USER + ADMIN)
       ===================================================== */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{quizId}/summary")
    public ResponseEntity<QuizResponseWithoutQuestionsDTO> getQuizSummary(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getQuizSummary(quizId));
    }

    /* =====================================================
       GET QUIZ WITH QUESTIONS (ADMIN ONLY)
       ===================================================== */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseWithQuestionsDTO> getQuizWithQuestions(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getQuizWithQuestions(quizId));
    }

    /* =====================================================
       START QUIZ (USER ONLY)
       ===================================================== */
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
    }
}
