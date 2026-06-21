package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Paper.PaperRequestDTO;
import getintouch.com.GetInTouch.DTO.Paper.PaperResponseDTO;
import getintouch.com.GetInTouch.Service.File.FileUploadService;
import getintouch.com.GetInTouch.Service.Paper.PaperService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
@Tag(name = "Paper Management", description = "APIs for managing Exam Papers")
@SecurityRequirement(name = "bearerAuth") // Links this controller to Swagger Security
public class PaperController {

    private final PaperService paperService;

    private final FileUploadService uploadService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new paper", description = "Accessible only by ADMIN users.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paper created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or Paper number already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<PaperResponseDTO> createPaper(@Valid @RequestBody PaperRequestDTO dto) {
        PaperResponseDTO response = paperService.createPaper(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing paper", description = "Accessible only by ADMIN users.")
    public ResponseEntity<PaperResponseDTO> updatePaper(
            @PathVariable Long id,
            @Valid @RequestBody PaperRequestDTO dto
    ) {
        PaperResponseDTO response = paperService.updatePaper(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a paper", description = "Sets the paper active flag to false. Admin only.")
    public ResponseEntity<Void> deletePaper(@PathVariable Long id) {
        paperService.deletePaper(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Re-activate a paper", description = "Sets the paper active flag to true. Admin only.")
    public ResponseEntity<PaperResponseDTO> activatePaper(@PathVariable Long id) {
        PaperResponseDTO response = paperService.activatePaper(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all papers", description = "Fetches both active and inactive papers.")
    public ResponseEntity<List<PaperResponseDTO>> getAllPapers() {
        return ResponseEntity.ok(paperService.getAllPapers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get paper by ID", description = "Fetches details of a specific active paper.")
    public ResponseEntity<PaperResponseDTO> getPaperById(@PathVariable Long id) {
        return ResponseEntity.ok(paperService.getPaperById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all active papers", description = "Returns a sorted list of all active papers.")
    public ResponseEntity<List<PaperResponseDTO>> getAllActivePapers() {
        return ResponseEntity.ok(paperService.getAllActivePapers());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all papers by Course ID")
    public ResponseEntity<List<PaperResponseDTO>> getPapersByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(paperService.getPapersByCourseId(courseId));
    }

    @GetMapping("/course/{courseId}/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get active papers by Course ID")
    public ResponseEntity<List<PaperResponseDTO>> getActivePapersByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(paperService.getActivePapersByCourseId(courseId));
    }

    @GetMapping("/course/{courseId}/paper-number/{paperNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get unique paper by Course ID and Paper Number")
    public ResponseEntity<PaperResponseDTO> getPaperByCourseIdAndPaperNumber(
            @PathVariable Long courseId,
            @PathVariable Long paperNumber
    ) {
        PaperResponseDTO response = paperService.getPaperByCourseIdAndPaperNumber(courseId, paperNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Search active papers by keyword name")
    public ResponseEntity<List<PaperResponseDTO>> searchPapers(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(paperService.searchPapers(keyword));
    }

    @Operation(summary = "Create Thumbnail", description = "Create a new Thumbnail For Papers (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Thumbnail created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PostMapping("/thumbnail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSlider(
            @RequestParam MultipartFile file) {
        String url=uploadService.uploadFile(file,"paper");
        return ResponseEntity.ok(url);
    }
}
