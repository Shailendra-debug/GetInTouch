package getintouch.com.GetInTouch.Service.Question;



import getintouch.com.GetInTouch.DTO.Question.QuestionCreateRequestDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionUpdateRequestDto;

import java.util.List;

public interface QuestionService {

    QuestionResponseDto create(QuestionCreateRequestDto request);

    QuestionResponseDto getById(Long id);

    List<QuestionResponseDto> getAll();

    QuestionResponseDto update(Long id, QuestionUpdateRequestDto request);

    void delete(Long id);
}