package getintouch.com.GetInTouch.Service.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesRequestDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseForUserDto;
import getintouch.com.GetInTouch.DTO.Payment.PaymentInitiateResponseDTO;
import getintouch.com.GetInTouch.DTO.Payment.PaymentRequestDTO;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Entity.Note.Purchase;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import getintouch.com.GetInTouch.Entity.Razorpay.PaymentStatus;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Repository.NotesRepository;
import getintouch.com.GetInTouch.Repository.PaperRepository;
import getintouch.com.GetInTouch.Repository.PurchaseRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import getintouch.com.GetInTouch.Service.Auth.EmailService;
import getintouch.com.GetInTouch.Service.Payments.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final PaymentService paymentService;
    private final PurchaseRepository purchaseRepository;
    private final EmailService emailService;

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
        notes.setActive(dto.getActive());

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
        log.debug("Fetching all notes from master catalog");
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
            throw new ResourceNotFoundException("Note not found with id: " + id);
        }
        notesRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotesResponseDto> getAllActive() {
        log.debug("Fetching all active notes");
        return notesRepository.findByActiveTrue()
                .stream()
                .map(this::mapToDto)
                .toList();
    }
    public List<NotesResponseDto> getDeactiveNotes() {
        return notesRepository.findAllByActiveFalse()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public NotesResponseDto activateNote(Long id) {
        Notes note = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        note.setActive(true);
        Notes updatedNote = notesRepository.save(note);

        return mapToDto(updatedNote); // Convert entity to your DTO here
    }

    @Override
    @Transactional
    public NotesResponseDto deactivateNote(Long id) {
        Notes note = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        note.setActive(false);
        Notes updatedNote = notesRepository.save(note);

        return mapToDto(updatedNote); // Convert entity to your DTO here
    }

    @Override
    public List<NotesResponseForUserDto> findByPaperId(Long paperId) {
        return notesRepository.findByPaperId(paperId)
                .stream()
                .map(notes -> mapToUserDto(notes,false))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotesResponseForUserDto> getAllActiveForUser(Long userId,Long paperId) {
        // Safe & blazing fast database lookup returning ONLY primitive IDs
        Set<Long> purchasedNoteIds = purchaseRepository.findPurchasedNoteIdsByUserId(userId);

        // Fetch only active notes and map directly to DTOs
        return notesRepository.findByPaperId(paperId)
                .stream()
                .map(note -> mapToUserDto(
                        note,
                        purchasedNoteIds.contains(note.getId()) // Fast O(1) Set lookup
                ))
                .toList();
    }

    /**
     * FIXED: This method now returns ONLY the notes that the user has actually paid for.
     */
    @Transactional(readOnly = true)
    @Override
    public List<NotesResponseForUserDto> getAllActivePurchase(Long userId) {
        log.debug("Fetching purchased library items for User ID: {}", userId);

        // 1. Fetch only the notes from the purchase transaction history mapping directly
        return purchaseRepository.findByUserIdAndPaymentStatusOrderByPurchaseDateDesc(userId, "SUCCESS")
                .stream()
                .map(purchase -> {
                    Notes note = purchase.getNote();
                    NotesResponseForUserDto dto = mapToUserDto(note, true);
                    dto.setPdfUrl(note.getPdfUrl()); // Safe to inject since it's verified owned
                    return dto;
                }).toList();
    }

    /**
     * FIXED: Swapped out unsafe lazy references for eager validation matching active scope requirements.
     */
    @Override
    @Transactional(readOnly = true)
    public NotesResponseForUserDto getActiveById(Long id) {
        Notes note = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active note matching requested identity missing."));

        if (!note.getActive()) {
            throw new AccessDeniedException("This content profile has been archived by administrative policies.");
        }
        return mapToUserDto(note,false);
    }

    @Transactional(readOnly = true)
    @Override
    public NotesResponseForUserDto getActiveByIdForUser(Long notesId, Long userId) {
        // Eagerly load target Note or fail safely with clear 404 Exception
        Notes note = notesRepository.findById(notesId)
                .orElseThrow(() -> new ResourceNotFoundException("Requested Note not found with ID: " + notesId));

        if (!note.getActive()) {
            throw new AccessDeniedException("This content is no longer active or available.");
        }

        // Perform single hyper-fast database flag lookup via indexing layer
        boolean isPurchased = purchaseRepository.existsByUserIdAndNoteId(userId, notesId);

        NotesResponseForUserDto dto = mapToUserDto(note, isPurchased);

        if (isPurchased) {
            dto.setPdfUrl(note.getPdfUrl()); // Expose secure asset path strings
        } else {
            dto.setPdfUrl(null); // Structural mask masking downstream access
        }

        return dto;
    }

    @Override
    @Transactional
    public PaymentInitiateResponseDTO PurchaseNotesById(Long userId, Long notesId){
        // Safe findById tracking preventing cascading pipeline drops on dead inputs
        Notes note = notesRepository.findById(notesId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot initiate purchase order on dead entity ID."));
        try {
            System.out.println("Nots prise "+note.getPrice());

            return paymentService.initiatePayment(new PaymentRequestDTO(userId, notesId, note.getPrice(), "INR"));
        } catch (Exception e) {
            log.error("Failed to generate payment voucher order context with payment provider: ", e);
            throw new RuntimeException(e+"Transaction pipeline execution failed to materialize authorization signature.", e);
        }
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
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
    }

    private Notes mapToEntity(NotesRequestDto dto) {
        Notes notes = Notes.builder()
                .title(dto.getTitle())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .thumbnailUrl(dto.getThumbnailUrl())
                .pdfUrl(dto.getPdfUrl())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

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
                .pdfUrl(null) // Keep hidden on listing
                .paperId(paper != null ? paper.getId() : null)
                .paperName(paper != null ? paper.getName() : null)
                .active(notes.getActive())
                .createdAt(notes.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void grantManualAccessToUser(Long userId, Long notesId) {
        log.info("Admin initiating manual access grant. User ID: {}, Note ID: {}", userId, notesId);

        // 1. Verify the Note exists in your catalog
        Notes note = notesRepository.findById(notesId)
                .orElseThrow(() -> new ResourceNotFoundException("Target Note not found with ID: " + notesId));

        // 2. Verify the target User exists in your system
        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Target User not found with ID: " + userId));

        // 3. Idempotency Check: Prevent duplicate rows if they already own it
        boolean alreadyOwned = purchaseRepository.existsByUserIdAndNoteId(userId, notesId);
        if (alreadyOwned) {
            log.info("User {} already has access to Note ID: {}. Skipping duplicate assignment.", buyer.getEmail(), notesId);
            return;
        }

        // 4. Create an administrative audit trail record
        Purchase manualAccessRecord = Purchase.builder()
                .user(buyer)
                .note(note)
                .purchaseDate(LocalDateTime.now())
                .amountPaid(BigDecimal.ZERO) // Set price to 0.00 for manual admin overrides
                .paymentStatus("ADMIN_GRANTED") // Distinct status tracking for accounting audits
                .build();

        purchaseRepository.save(manualAccessRecord);

        emailService.sendPaymentStatusEmail(buyer.getEmail(), buyer.getFullName(), manualAccessRecord.getId().toString(),"By ADMIN","0", PaymentStatus.ADMIN_GRANTED.toString());
        log.info("Successfully granted manual access to Note: '{}' for User: {}", note.getTitle(), buyer.getEmail());
    }

    private NotesResponseDto mapToDtoForPublic(Notes notes) {
        Paper paper = notes.getPaper();

        return NotesResponseDto.builder()
                .id(notes.getId())
                .title(notes.getTitle())
                .price(notes.getPrice())
                .description(notes.getDescription())
                .thumbnailUrl(notes.getThumbnailUrl())
                .pdfUrl(null) // Safe default layout wrapper protecting core assets
                .paperId(paper != null ? paper.getId() : null)
                .paperName(paper != null ? paper.getName() : null)
                .active(notes.getActive())
                .createdAt(notes.getCreatedAt())
                .build();
    }

    private NotesResponseForUserDto mapToUserDto(Notes notes, boolean isPurchase) {
        Paper paper = notes.getPaper(); // Extracted for clean null check handling

        return NotesResponseForUserDto.builder()
                .id(notes.getId())
                .title(notes.getTitle())
                .price(notes.getPrice())
                .description(notes.getDescription())
                .thumbnailUrl(notes.getThumbnailUrl())
                .pdfUrl(null) // Handled dynamically outside inside explicit controllers
                .paperId(paper != null ? paper.getId() : null)   // ✅ FIXED: Now Null-safe!
                .paperName(paper != null ? paper.getName() : null) // ✅ FIXED: Now Null-safe!
                .isPurchase(isPurchase)
                .createdAt(notes.getCreatedAt())
                .build();
    }
}