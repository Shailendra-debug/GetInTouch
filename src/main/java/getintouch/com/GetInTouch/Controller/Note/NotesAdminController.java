package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesRequestDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseForUserDto;
import getintouch.com.GetInTouch.Service.File.FileUploadService;
import getintouch.com.GetInTouch.Service.Note.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/admin/notes")
@RequiredArgsConstructor
@Tag(name = "Admin Notes", description = "Admin APIs for Notes Management")
@Validated
public class NotesAdminController {

    private final NotesService service;
    private final FileUploadService fileUploadService;

    @Operation(
            summary = "Create Note",
            description = "Create a new note"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<NotesResponseDto> create(
            @Valid @RequestBody NotesRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @Operation(
            summary = "Update Note",
            description = "Update existing note"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<NotesResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody NotesRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
            summary = "Get Note By ID",
            description = "Fetch note details"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<NotesResponseDto> get(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "Get All Notes",
            description = "Fetch all notes"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<NotesResponseDto>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get all active notes")
    public ResponseEntity<List<NotesResponseDto>> getActiveNotes() {
        return ResponseEntity.ok(service.getAllActive());
    }

    @GetMapping("/deactive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all inactive notes")
    public ResponseEntity<List<NotesResponseDto>> getDeactiveNotes() {
        return ResponseEntity.ok(service.getDeactiveNotes());
    }

    @Operation(
            summary = "Delete Note",
            description = "Delete note permanently"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Upload Note PDF",
            description = "Upload PDF file to Cloudflare R2"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            value = "/upload_note",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadNote(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                fileUploadService.uploadFile(file, "notes")
        );
    }
    @PutMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate a specific Note")
    public ResponseEntity<NotesResponseDto> activateNote(@PathVariable Long id) {
        return ResponseEntity.ok(service.activateNote(id));
    }

    @PutMapping("/{id}/deactive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a specific Note")
    public ResponseEntity<NotesResponseDto> deactivateNote(@PathVariable Long id) {
        return ResponseEntity.ok(service.deactivateNote(id));
    }

    @Operation(
            summary = "Upload Thumbnail",
            description = "Upload thumbnail image to Cloudflare R2"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            value = "/upload_thumbnail",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadThumbnail(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                fileUploadService.uploadFile(file, "thumbnails")
        );
    }

    @Operation(summary = "Admin: Grant Manual Access", description = "Allows administrative accounts to manually unlock notes for any user ID")
    @PostMapping("/admin/notes/grant-access")
    @PreAuthorize("hasRole('ADMIN')") // Strict Role-Based Access Control (RBAC) via JWT
    public ResponseEntity<String> grantManualAccess(
            @RequestParam Long userId,
            @RequestParam Long notesId) {

        service.grantManualAccessToUser(userId, notesId);
        return ResponseEntity.ok("Access granted successfully to the user.");
    }
}
