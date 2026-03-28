package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesRequestDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;
import getintouch.com.GetInTouch.Service.File.FileUploadService;
import getintouch.com.GetInTouch.Service.Note.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/admin/notes")
@RequiredArgsConstructor
public class NotesAdminController {

    private final NotesService service;
    private final FileUploadService fileUploadService;

    @Operation(summary = "Create Note")
    @PostMapping
    public ResponseEntity<NotesResponseDto> create(
            @Valid @RequestBody NotesRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Update Note")
    @PutMapping("/{id}")
    public ResponseEntity<NotesResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody NotesRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Get Note by ID")
    @GetMapping("/{id}")
    public ResponseEntity<NotesResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Get All Notes")
    @GetMapping
    public ResponseEntity<List<NotesResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Delete Note")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // 📤 Upload Thumbnail / PDF / QR
    @Operation(summary = "Upload c (Cloudflare R2)")
    @PostMapping("/upload_note")
    public ResponseEntity<String> upload_Note(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileUploadService.uploadFile(file,"notes"));
    }

    @Operation(summary = "Upload Thumbnail (Cloudflare R2)")
    @PostMapping("/upload_thumbnail")
    public ResponseEntity<String> upload_Thumbnail(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileUploadService.uploadFile(file,"thumbnails"));
    }

    @Operation(summary = "Upload QR (Cloudflare R2)")
    @PostMapping("/upload_qr")
    public ResponseEntity<String> upload_QR(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileUploadService.uploadFile(file,"paymentQr"));
    }

}
