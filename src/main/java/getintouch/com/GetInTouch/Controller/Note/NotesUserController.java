package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesResponseForUserDto;
import getintouch.com.GetInTouch.DTO.Note.NotesUserResponse;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Service.Note.NotesService;
import getintouch.com.GetInTouch.Service.Note.NotesServiceImpl;
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

    private final NotesService notesService;

    @Operation(summary = "Get all Purchase notes for logged-in user")
    @GetMapping("/purchased")
    public ResponseEntity<List<NotesResponseForUserDto>> getApproved() {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                notesService.getAllActivePurchase(currentUser)
        );
    }

    @Operation(summary = "Get all notes")
    @GetMapping
    public ResponseEntity<List<NotesResponseForUserDto>> getAll() {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                notesService.getAllActiveForUser(currentUser)
        );
    }

    @Operation(summary = "Get Purchase note by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Notes> getById(
            @PathVariable Long id) {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                service.getApprovedNote(currentUser, id)
        );
    }


}