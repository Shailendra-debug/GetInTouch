package getintouch.com.GetInTouch.Service.Note;

import getintouch.com.GetInTouch.DTO.Note.NotesRequestDto;
import getintouch.com.GetInTouch.DTO.Note.NotesResponseDto;

import java.util.List;

public interface NotesService {

    NotesResponseDto create(NotesRequestDto dto);

    NotesResponseDto update(Long id, NotesRequestDto dto);

    NotesResponseDto getById(Long id);

    List<NotesResponseDto> getAll();

    void delete(Long id);

    List<NotesResponseDto> getAllActive();

    NotesResponseDto getActiveById(Long id);

    NotesResponseDto activate(Long id);

    NotesResponseDto deactivate(Long id);
}