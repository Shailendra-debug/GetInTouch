package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterRequestDTO;
import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import getintouch.com.GetInTouch.Service.Chapter.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter Management", description = "Secure APIs for managing Chapter records")
@SecurityRequirement(name = "bearerAuth")
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new chapter", description = "Accessible only by ADMIN users.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chapter created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or duplicate chapter number")
    })
    public ResponseEntity<ChapterResponseDTO> createChapter(@Valid @RequestBody ChapterRequestDTO dto) {
        ChapterResponseDTO response = chapterService.createChapter(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing chapter", description = "Accessible only by ADMIN users.")
    public ResponseEntity<ChapterResponseDTO> updateChapter(
            @PathVariable Long id,
            @Valid @RequestBody ChapterRequestDTO dto
    ) {
        ChapterResponseDTO response = chapterService.updateChapter(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a chapter", description = "Sets active flag to false. Admin only.")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Re-activate a chapter", description = "Sets active flag to true. Admin only.")
    public ResponseEntity<ChapterResponseDTO> activateChapter(@PathVariable Long id) {
        ChapterResponseDTO response = chapterService.activateChapter(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Get all chapters", description = "Fetches both active and inactive chapters.")
    public ResponseEntity<List<ChapterResponseDTO>> getAllChapters() {
        return ResponseEntity.ok(chapterService.getAllChapters());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get chapter by ID")
    public ResponseEntity<ChapterResponseDTO> getChapterById(@PathVariable Long id) {
        return ResponseEntity.ok(chapterService.getChapterById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all active chapters")
    public ResponseEntity<List<ChapterResponseDTO>> getAllActiveChapters() {
        return ResponseEntity.ok(chapterService.getAllActiveChapters());
    }

    @GetMapping("/paper/{paperId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all chapters of a specific Paper")
    public ResponseEntity<List<ChapterResponseDTO>> getChaptersByPaperId(@PathVariable Long paperId) {
        return ResponseEntity.ok(chapterService.getChaptersByPaperId(paperId));
    }

    @GetMapping("/paper/{paperId}/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get active chapters of a specific Paper")
    public ResponseEntity<List<ChapterResponseDTO>> getActiveChaptersByPaperId(@PathVariable Long paperId) {
        return ResponseEntity.ok(chapterService.getActiveChaptersByPaperId(paperId));
    }

    @GetMapping("/paper/{paperId}/chapter-number/{chapterNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get chapter by Paper ID and Chapter Number")
    public ResponseEntity<ChapterResponseDTO> getChapterByPaperIdAndChapterNumber(
            @PathVariable Long paperId,
            @PathVariable Long chapterNumber
    ) {
        ChapterResponseDTO response = chapterService.getChapterByPaperIdAndChapterNumber(paperId, chapterNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Search active chapters by title keyword")
    public ResponseEntity<List<ChapterResponseDTO>> searchChapters(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(chapterService.searchChapters(keyword));
    }
}