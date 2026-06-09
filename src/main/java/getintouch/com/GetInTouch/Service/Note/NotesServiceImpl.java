package getintouch.com.GetInTouch.Service.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesRequestDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseForUserDto;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.NotesRepository;
import getintouch.com.GetInTouch.Repository.PaperRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotesResponseDto create(NotesRequestDto dto) {
        log.info("Creating new note with title: {}", dto.getTitle());
        Notes notes = mapToEntity(dto);
        Notes savedNotes = notesRepository.save(notes);
        return mapToDto(savedNotes);
    }

    @Override
    @Transactional
    public NotesResponseDto update(Long id, NotesRequestDto dto) {
        log.info("Updating note with id: {}", id);
        Notes notes = getNoteOrThrow(id);

        notes.setTitle(dto.getTitle());
        notes.setPrice(dto.getPrice());
        notes.setDescription(dto.getDescription());
        notes.setThumbnailUrl(dto.getThumbnailUrl());
        notes.setPdfUrl(dto.getPdfUrl());
        notes.setActive(dto.getActive());

        // Update Paper relationship if provided
        if (dto.getPaperId() != null) {
            notes.setPaper(paperRepository.getReferenceById(dto.getPaperId()));
        }

        return mapToDto(notesRepository.save(notes));
    }

    @Override
    @Transactional(readOnly = true)
    public NotesResponseDto getById(Long id) {
        log.debug("Fetching note by id: {}", id);
        return mapToDto(getNoteOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotesResponseDto> getAll() {
        log.debug("Fetching all notes");
        return notesRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.warn("Permanently deleting note with id: {}", id);
        if (!notesRepository.existsById(id)) {
            throw new EntityNotFoundException("Note not found with id: " + id);
        }
        notesRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotesResponseDto> getAllActive() {
        log.debug("Fetching all active notes");
        return notesRepository.findByActiveTrue()
                .stream()
                .map(this::mapToDtoForPublic)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotesResponseForUserDto> getAllActiveForUser(Long id) {

        User user = userRepository.getReferenceById(id);

        Set<Long> purchasedNoteIds = user.getPurchasedNotes()
                .stream()
                .map(Notes::getId)
                .collect(Collectors.toSet());

        return notesRepository.findByActiveTrue()
                .stream()
                .map(note -> mapToUserDto(
                        note,
                        purchasedNoteIds.contains(note.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotesResponseForUserDto> getAllActivePurchase(Long id) {

        User user = userRepository.getReferenceById(id);

        return notesRepository.findByActiveTrue()
                .stream()
                .map(note -> mapToUserDto(
                        note,
                        true
                ))
                .toList();
    }

    @Override
    public NotesResponseDto getActiveById(Long id) {
        return mapToDtoForPublic(notesRepository.getReferenceById(id));
    }


    @Transactional(readOnly = true)
    @Override
    public NotesResponseForUserDto getActiveByIdForUser(Long notesId, Long userId) {

        User user=userRepository.getReferenceById(userId);

        Set<Long> purchasedNoteIds = user.getPurchasedNotes()
                .stream()
                .map(Notes::getId)
                .collect(Collectors.toSet());

       Notes notes= notesRepository.getReferenceById(notesId);

       if (purchasedNoteIds.contains(notes.getId())){
           NotesResponseForUserDto dto=mapToUserDto(notes,true);
           dto.setPdfUrl(notes.getPdfUrl());
       }
        return mapToUserDto(notes,false);
    }



    @Override
    @Transactional
    public NotesResponseDto activate(Long id) {
        log.info("Activating note with id: {}", id);
        Notes notes = getNoteOrThrow(id);
        notes.setActive(true);
        return mapToDto(notesRepository.save(notes));
    }

    @Override
    @Transactional
    public NotesResponseDto deactivate(Long id) {
        log.info("Deactivating (soft deleting) note with id: {}", id);
        Notes notes = getNoteOrThrow(id);
        notes.setActive(false);
        return mapToDto(notesRepository.save(notes));
    }

    // =========================
    // Helper & Mapping Methods
    // =========================

    private Notes getNoteOrThrow(Long id) {
        return notesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + id));
    }

    private Notes mapToEntity(NotesRequestDto dto) {
        Notes notes = Notes.builder()
                .title(dto.getTitle())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .thumbnailUrl(dto.getThumbnailUrl())
                .pdfUrl(dto.getPdfUrl())
                .active(dto.getActive())
                .build();

        // Attach Paper reference without fetching from DB (uses JPA Proxy)
        if (dto.getPaperId() != null) {
            notes.setPaper(paperRepository.getReferenceById(dto.getPaperId()));
        }

        return notes;
    }

    private NotesResponseDto mapToDto(Notes notes) {
        Paper paper = notes.getPaper();

        return NotesResponseDto.builder()
                .id(notes.getId())
                .title(notes.getTitle())
                .price(notes.getPrice())
                .description(notes.getDescription())
                .thumbnailUrl(notes.getThumbnailUrl())
                .pdfUrl(null)
                .paperId(paper != null ? paper.getId() : null)
                .paperName(paper != null ? paper.getName() : null)
                .active(notes.getActive())
                .createdAt(notes.getCreatedAt())
                .build();
    }
    private NotesResponseDto mapToDtoForPublic(Notes notes) {
        Paper paper = notes.getPaper();

        return NotesResponseDto.builder()
                .id(notes.getId())
                .title(notes.getTitle())
                .price(notes.getPrice())
                .description(notes.getDescription())
                .thumbnailUrl(notes.getThumbnailUrl())
                .pdfUrl(null)
                .paperId(paper != null ? paper.getId() : null)
                .paperName(paper != null ? paper.getName() : null)
                .active(notes.getActive())
                .createdAt(notes.getCreatedAt())
                .build();
    }

    private NotesResponseForUserDto mapToUserDto(Notes notes, boolean isPurchase) {

        return NotesResponseForUserDto.builder()
                .id(notes.getId())
                .title(notes.getTitle())
                .price(notes.getPrice())
                .description(notes.getDescription())
                .thumbnailUrl(notes.getThumbnailUrl())
                .pdfUrl(null)
                .paperId(notes.getPaper().getId())
                .paperName(notes.getPaper().getName())
                .isPurchase(isPurchase)
                .createdAt(notes.getCreatedAt())
                .build();
    }
}