package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesUserResponse;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Service.Note.NotesUserService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "User Notes", description = "User notes access APIs")
@PreAuthorize("isAuthenticated()")
public class NotesUserController {

    private final NotesUserService service;

    @Operation(summary = "Get all approved notes for logged-in user")
    @GetMapping("/approved")
    public ResponseEntity<List<NotesUserResponse>> getApproved() {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                service.getApprovedNotes(currentUser)
        );
    }

    @Operation(summary = "Get all notes with access status")
    @GetMapping
    public ResponseEntity<List<NotesUserResponse>> getAll() {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                service.getAllNotesWithStatus(currentUser)
        );
    }

    @Operation(summary = "Get approved note by ID")
    @GetMapping("/approved/{id}")
    public ResponseEntity<Notes> getById(
            @PathVariable Long id) {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                service.getApprovedNote(currentUser, id)
        );
    }
}