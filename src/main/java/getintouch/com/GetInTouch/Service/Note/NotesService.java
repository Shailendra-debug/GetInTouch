package getintouch.com.GetInTouch.Service.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesRequestDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseForUserDto;
import getintouch.com.GetInTouch.DTO.Payment.PaymentInitiateResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotesService {

    NotesResponseDto create(NotesRequestDto dto);

    NotesResponseDto update(Long id, NotesRequestDto dto);

    NotesResponseDto getById(Long id);

    List<NotesResponseDto> getAll();

    void delete(Long id);

    List<NotesResponseDto> getAllActive();


    List<NotesResponseDto> findByPaperId(Long paperId);

    @Transactional(readOnly = true)
    List<NotesResponseForUserDto> getAllActiveForUser(Long id,Long paperId);

    @Transactional(readOnly = true)
    List<NotesResponseForUserDto> getAllActivePurchase(Long id);

    NotesResponseDto getActiveById(Long id);

    @Transactional(readOnly = true)
    NotesResponseForUserDto getActiveByIdForUser(Long notesId, Long userId);

    PaymentInitiateResponseDTO PurchaseNotesById(Long userId,Long notesId);

    NotesResponseDto activate(Long id);

    NotesResponseDto deactivate(Long id);

    void grantManualAccessToUser(Long userId, Long notesId);
}