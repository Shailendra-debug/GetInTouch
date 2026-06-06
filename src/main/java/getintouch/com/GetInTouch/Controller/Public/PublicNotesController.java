package getintouch.com.GetInTouch.Controller.Public;

import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;
import getintouch.com.GetInTouch.Service.Note.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/notes")
@RequiredArgsConstructor
@Tag(name = "Public Notes", description = "Public APIs for Notes")
public class PublicNotesController {

    private final NotesService notesService;

    @Operation(
            summary = "Get All Active Notes",
            description = "Returns all active notes available for purchase"
    )
    @GetMapping
    public ResponseEntity<List<NotesResponseDto>> getAllActiveNotes() {

        return ResponseEntity.ok(
                notesService.getAllActive()
        );
    }

    @Operation(
            summary = "Get Active Note By ID",
            description = "Returns note details if note is active"
    )
    @GetMapping("/{id}")
    public ResponseEntity<NotesResponseDto> getNoteById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notesService.getActiveById(id)
        );
    }
}
