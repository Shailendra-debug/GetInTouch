package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Question.QuestionCreateRequestDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionUpdateRequestDto;
import getintouch.com.GetInTouch.Service.Question.QuestionService;
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
public class QuestionController {

    private final QuestionService questionService;

    /* ---------- CREATE ---------- */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponseDto> create(
            @Valid @RequestBody QuestionCreateRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(questionService.create(request));
    }

    /* ---------- READ ---------- */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<QuestionResponseDto> getById(@PathVariable Long id) {

        return ResponseEntity.ok(questionService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<QuestionResponseDto>> getAll() {

        return ResponseEntity.ok(questionService.getAll());
    }

    /* ---------- UPDATE ---------- */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponseDto> update(
            @PathVariable Long id,
            @RequestBody QuestionUpdateRequestDto request) {

        return ResponseEntity.ok(questionService.update(id, request));
    }

    /* ---------- DELETE ---------- */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
