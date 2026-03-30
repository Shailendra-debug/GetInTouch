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

    public NotesResponseDto create(NotesRequestDto dto) {
        Notes notes = mapToEntity(dto);
        return mapToDto(repository.save(notes));
    }

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

    public NotesResponseDto getById(Long id) {
        return mapToDto(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found")));
    }

    public List<NotesResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // 🔄 Mapping
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