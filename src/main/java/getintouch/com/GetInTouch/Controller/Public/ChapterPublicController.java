package getintouch.com.GetInTouch.Controller.Public;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import getintouch.com.GetInTouch.Service.Chapter.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/chapters")
@RequiredArgsConstructor
@Tag(name = "Public Chapter View APIs", description = "Open APIs accessible to guests without logging in")
public class ChapterPublicController {

    private final ChapterService chapterService;

    @GetMapping("/active")
    @Operation(summary = "Get all active chapters for guests")
    public ResponseEntity<List<ChapterResponseDTO>> getAllActiveChapters() {
        return ResponseEntity.ok(chapterService.getAllActiveChapters());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get active chapter details by ID for guests")
    public ResponseEntity<ChapterResponseDTO> getChapterById(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getChapterById(id));
    }

    @GetMapping("/paper/{paperId}/active")
    @Operation(summary = "Get active chapters belonging to a paper for guests")
    public ResponseEntity<List<ChapterResponseDTO>> getActiveChaptersByPaperId(@PathVariable Long paperId) {
        return ResponseEntity.ok(chapterService.getActiveChaptersByPaperId(paperId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search active chapters by keyword for guests")
    public ResponseEntity<List<ChapterResponseDTO>> searchChapters(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(chapterService.searchChapters(keyword));
    }
}
