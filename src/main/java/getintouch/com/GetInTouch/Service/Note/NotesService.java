package getintouch.com.GetInTouch.Service.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesRequestDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Repository.NotesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesService {

    private final NotesRepository repository;

    // Create
    public NotesResponseDto create(NotesRequestDto dto) {
        Notes notes = mapToEntity(dto);
        return mapToDto(repository.save(notes));
    }

    // Update
    public NotesResponseDto update(Long id, NotesRequestDto dto) {
        Notes notes = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        notes.setTitle(dto.getTitle());
        notes.setPrice(dto.getPrice());
        notes.setDescription(dto.getDescription());
        notes.setThumbnailUrl(dto.getThumbnailUrl());
        notes.setPdfUrl(dto.getPdfUrl());
        notes.setPaymentQrUrl(dto.getPaymentQrUrl());
        notes.setActive(dto.getActive());

        return mapToDto(repository.save(notes));
    }

    // Get By Id
    public NotesResponseDto getById(Long id) {
        return mapToDto(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found")));
    }

    // Get All
    public List<NotesResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // Delete Permanently
    public void delete(Long id) {
        repository.deleteById(id);
    }


    // Get only active notes
    public List<NotesResponseDto> getAllActive() {
        return repository.findByActiveTrue()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // Get active note by id
    public NotesResponseDto getActiveById(Long id) {
        return mapToDto(
                repository.findByIdAndActiveTrue(id)
                        .orElseThrow(() -> new RuntimeException("Active note not found"))
        );
    }

    // Soft Delete
    public void softDelete(Long id) {
        Notes notes = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        notes.setActive(false);
        repository.save(notes);
    }

    // Activate Note
    public NotesResponseDto activate(Long id) {
        Notes notes = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        notes.setActive(true);
        return mapToDto(repository.save(notes));
    }

    // Deactivate Note
    public NotesResponseDto deactivate(Long id) {
        Notes notes = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        notes.setActive(false);
        return mapToDto(repository.save(notes));
    }

    // =========================
    // Mapping Methods
    // =========================

    private Notes mapToEntity(NotesRequestDto dto) {
        return Notes.builder()
                .title(dto.getTitle())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .thumbnailUrl(dto.getThumbnailUrl())
                .pdfUrl(dto.getPdfUrl())
                .paymentQrUrl(dto.getPaymentQrUrl())
                .active(dto.getActive())
                .build();
    }

    private NotesResponseDto mapToDto(Notes notes) {
        return NotesResponseDto.builder()
                .id(notes.getId())
                .title(notes.getTitle())
                .price(notes.getPrice())
                .description(notes.getDescription())
                .thumbnailUrl(notes.getThumbnailUrl())
                .pdfUrl(notes.getPdfUrl())
                .paymentQrUrl(notes.getPaymentQrUrl())
                .active(notes.getActive())
                .createdAt(notes.getCreatedAt())
                .build();
    }
}