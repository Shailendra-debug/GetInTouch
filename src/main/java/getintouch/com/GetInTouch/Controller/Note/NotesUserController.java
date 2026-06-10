package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesResponseForUserDto;
import getintouch.com.GetInTouch.DTO.Payment.PaymentInitiateResponseDTO;
import getintouch.com.GetInTouch.Service.Note.NotesService;
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
    @GetMapping("/paper/{id}")
    public ResponseEntity<List<NotesResponseForUserDto>> getAll(@PathVariable Long paperId) {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                notesService.getAllActiveForUser(currentUser,paperId)
        );
    }

    @Operation(summary = "Get Purchase note by ID")
    @GetMapping("/{id}")
    public ResponseEntity<NotesResponseForUserDto> getById(
            @PathVariable Long id   ) {

        Long currentUser = SecurityUtil.getCurrentUserId();

        return ResponseEntity.ok(
                notesService.getActiveByIdForUser(id,currentUser)
        );
    }

    @Operation(summary = "Get Purchase note by ID")
    @GetMapping("/purchase/{id}")
    public ResponseEntity<PaymentInitiateResponseDTO> bayNores(
            @PathVariable Long notesId) {
        Long currentUser = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(notesService.PurchaseNotesById(currentUser,notesId));
    }


}