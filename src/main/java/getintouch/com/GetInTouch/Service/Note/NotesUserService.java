package getintouch.com.GetInTouch.Service.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesUserResponse;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Repository.NotesRepository;
import getintouch.com.GetInTouch.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesUserService {

    private final NotesRepository notesRepository;
    private final OrderRepository orderRepository;

    // ✅ 1. Only Approved Notes
    public List<NotesUserResponse> getApprovedNotes(Long userId) {

        List<Long> approvedIds = orderRepository.findApprovedNoteIds(userId);

        return notesRepository.findAllById(approvedIds)
                .stream()
                .map(note -> NotesUserResponse.builder()
                        .id(note.getId())
                        .title(note.getTitle())
                        .price(note.getPrice().doubleValue())
                        .thumbnailUrl(note.getThumbnailUrl())
                        .approved(true)
                        .build())
                .toList();
    }

    // ✅ 2. All Notes + Status
    public List<NotesUserResponse> getAllNotesWithStatus(Long userId) {

        List<Long> approvedIds = orderRepository.findApprovedNoteIds(userId);

        return notesRepository.findAll()
                .stream()
                .map(note -> NotesUserResponse.builder()
                        .id(note.getId())
                        .title(note.getTitle())
                        .price(note.getPrice().doubleValue())
                        .thumbnailUrl(note.getThumbnailUrl())
                        .approved(approvedIds.contains(note.getId()))
                        .build())
                .toList();
    }
    public Notes getApprovedNote(Long userId, Long orderId) {
        return orderRepository.findApprovedNote(userId, orderId)
                .orElseThrow(() -> new RuntimeException("Note not accessible"));
    }
}