package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesUserResponse;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Service.Note.NotesUserService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NotesUserController {

    private final NotesUserService service;

    // ✅ Only approved notes



    @GetMapping("/approved")
    public ResponseEntity<List<NotesUserResponse>> getApproved(
    ) {
        Long currentUser = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.getApprovedNotes(currentUser));
    }

    // ✅ All notes + status
    @GetMapping
    public ResponseEntity<List<NotesUserResponse>> getAll(
    ) {
        Long currentUser = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.getAllNotesWithStatus(currentUser));
    }
    @GetMapping("/approved/{id}")
    public ResponseEntity<Notes> getById(@PathVariable Long id
    ) {
        Long currentUser = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.getApprovedNote(currentUser,id));
    }
}