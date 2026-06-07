package getintouch.com.GetInTouch.Controller.Public;

import getintouch.com.GetInTouch.DTO.Paper.PaperResponseDTO;
import getintouch.com.GetInTouch.Service.Paper.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/papers") // Notice the "/public" path prefix
@RequiredArgsConstructor
@Tag(name = "Public Paper View APIs", description = "Open APIs accessible to guests without logging in")
public class PaperPublicController {

    private final PaperService paperService;

    @GetMapping()
    @Operation(summary = "Get all active papers for guests")
    public ResponseEntity<List<PaperResponseDTO>> getAllActivePapers() {
        return ResponseEntity.ok(paperService.getAllActivePapers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get active paper details by ID for guests")
    public ResponseEntity<PaperResponseDTO> getPaperById(@PathVariable Long id) {
        return ResponseEntity.ok(paperService.getPaperById(id));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get active papers belonging to a course for guests")
    public ResponseEntity<List<PaperResponseDTO>> getActivePapersByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(paperService.getActivePapersByCourseId(courseId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search active papers by keyword for guests")
    public ResponseEntity<List<PaperResponseDTO>> searchPapers(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(paperService.searchPapers(keyword));
    }
}
