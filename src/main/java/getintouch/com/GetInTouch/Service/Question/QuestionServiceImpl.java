package getintouch.com.GetInTouch.Service.Question;

import getintouch.com.GetInTouch.DTO.Question.QuestionCreateRequestDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionUpdateRequestDto;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Quiz.Course;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.QuestionMapper;
import getintouch.com.GetInTouch.Repository.CourseRepository;
import getintouch.com.GetInTouch.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final QuestionMapper questionMapper;

    /* ---------- CREATE ---------- */

    @Override
    public QuestionResponseDto create(QuestionCreateRequestDto request) {

        if (questionRepository.existsByQuestion(request.getQuestion())) {
            throw new BadRequestException("Question already exists");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found id " + request.getCourseId()));

        validateCorrectIndexes(request.getOptions(), request.getCorrect());

        Question question = questionMapper.toEntity(request);
        question.setCourse(course);

        Question saved = questionRepository.save(question);

        QuestionResponseDto dto = questionMapper.toDto(saved);
        dto.setCourseName(course.getName());

        return dto;
    }

    /* ---------- READ ---------- */

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDto getById(Long id) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found with id: " + id));

        QuestionResponseDto dto = questionMapper.toDto(question);
        dto.setCourseName(question.getCourse().getName());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getAll() {

        return questionRepository.findAll()
                .stream()
                .map(question -> {
                    QuestionResponseDto dto = questionMapper.toDto(question);
                    dto.setCourseName(question.getCourse().getName());
                    return dto;
                })
                .toList();
    }

    /* ---------- UPDATE ---------- */

    @Override
    public QuestionResponseDto update(Long id, QuestionUpdateRequestDto request) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found with id: " + id));

        if (request.getOptions() != null && request.getCorrect() != null) {
            validateCorrectIndexes(request.getOptions(), request.getCorrect());
        }

        Course course = question.getCourse();
        if (request.getCourseId() != null) {
            course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Course not found id " + request.getCourseId()));
            question.setCourse(course);
        }

        questionMapper.updateEntity(request, question);

        Question updated = questionRepository.save(question);

        QuestionResponseDto dto = questionMapper.toDto(updated);
        dto.setCourseName(course.getName());

        return dto;
    }

    /* ---------- DELETE ---------- */

    @Override
    public void delete(Long id) {

        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Question not found with id: " + id);
        }

        questionRepository.deleteById(id);
    }

    /* ---------- VALIDATION ---------- */

    private void validateCorrectIndexes(
            List<String> options,
            List<Integer> correctIndexes
    ) {

        if (options == null || options.isEmpty()) {
            throw new BadRequestException("Options must not be empty");
        }

        if (correctIndexes == null || correctIndexes.isEmpty()) {
            throw new BadRequestException("Correct answer must be provided");
        }

        for (Integer index : correctIndexes) {
            if (index < 0 || index >= options.size()) {
                throw new BadRequestException(
                        "Correct answer index out of range");
            }
        }
    }
}
