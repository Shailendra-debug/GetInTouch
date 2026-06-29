package getintouch.com.GetInTouch.Service.Question;

import getintouch.com.GetInTouch.DTO.Question.QuestionCreateRequestDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseDto;
import getintouch.com.GetInTouch.DTO.Question.QuestionUpdateRequestDto;
import getintouch.com.GetInTouch.Entity.Question.Option;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.QuestionMapper;
import getintouch.com.GetInTouch.Repository.ChapterRepository;
import getintouch.com.GetInTouch.Repository.PaperRepository;
import getintouch.com.GetInTouch.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // Optimize read operations
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ChapterRepository chapterRepository;
    private final QuestionMapper questionMapper;
    private final PaperRepository paperRepository;

    /* ---------- CREATE ---------- */

    @Override
    @Transactional
    public QuestionResponseDto create(QuestionCreateRequestDto request) {
        log.info("Creating question for Chapter ID: {}", request.getChapterId());

        // Prevents identical questions from being added to the SAME chapter
        if (questionRepository.existsByQuestionAndChapterId(request.getQuestion(), request.getChapterId())) {
            throw new BadRequestException("This question already exists in this chapter.");
        }

        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found id " + request.getChapterId()));

        if (!chapter.getActive()) {
            throw new BadRequestException("Cannot add questions to an inactive chapter.");
        }

        validateCorrectIndexes(request.getOptions(), request.getCorrect());

        Question question = questionMapper.toEntity(request);
        question.setChapter(chapter);
        question.setActive(true); // Ensure new questions default to active

        Question saved = questionRepository.save(question);
        return mapToDtoWithChapter(saved, chapter);
    }


    @Override
    @Transactional
    public List<QuestionResponseDto> createListOfQus(
            List<QuestionCreateRequestDto> request
    ) {

        Set<Long> chapterIds = request.stream()
                .map(QuestionCreateRequestDto::getChapterId)
                .collect(Collectors.toSet());

        Map<Long, Chapter> chapterMap =
                chapterRepository.findAllById(chapterIds)
                        .stream()
                        .collect(Collectors.toMap(
                                Chapter::getId,
                                Function.identity()
                        ));

        List<Question> questionList = new ArrayList<>();

        for (QuestionCreateRequestDto dto : request) {

            if (!chapterMap.containsKey(dto.getChapterId())) {
                throw new ResourceNotFoundException(
                        "Chapter not found id "
                                + dto.getChapterId()
                );
            }

            Chapter chapter =
                    chapterMap.get(dto.getChapterId());

            if (!chapter.getActive()) {
                throw new BadRequestException(
                        "Cannot add questions to an inactive chapter."
                );
            }

            validateCorrectIndexes(
                    dto.getOptions(),
                    dto.getCorrect()
            );

            if (questionRepository.existsByQuestionAndChapterId(
                    dto.getQuestion(),
                    dto.getChapterId()
            )) {

                throw new BadRequestException(
                        "Question already exists in chapter: "
                                + dto.getQuestion()
                );
            }

            Question question =
                    questionMapper.toEntity(dto);

            question.setChapter(chapter);
            question.setActive(true);

            questionList.add(question);
        }

        List<Question> savedQuestions =
                questionRepository.saveAll(questionList);

        return savedQuestions.stream()
                .map(q ->
                        mapToDtoWithChapter(
                                q,
                                q.getChapter()
                        ))
                .toList();
    }



    /* ---------- READ (Standard CRUD) ---------- */

    @Override
    public QuestionResponseDto getById(Long id) {
        Question question = questionRepository.findCompleteQuestionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active question not found with id: " + id));

        return mapToDtoWithChapter(question, question.getChapter());
    }

    @Override
    public List<QuestionResponseDto> getAll() {

        return questionRepository.findAllWithChapter()
                .stream()
                .map(question ->
                        mapToDtoWithChapter(
                                question,
                                question.getChapter()
                        ))
                .toList();
    }

    public List<QuestionResponseDto> getAllActiveQuestions() {
        List<Question> activeQuestions = questionRepository.findByActiveTrue();
        return activeQuestions.stream()
                .map(question ->
                        mapToDtoWithChapter(
                                question,
                                question.getChapter()
                        ))
                .toList();
    }

    // --- Inactive Questions Service ---
    public List<QuestionResponseDto> getAllInactiveQuestions() {
        List<Question> inactiveQuestions = questionRepository.findByActiveFalse();
        return inactiveQuestions.stream()
                .map(question ->
                        mapToDtoWithChapter(
                                question,
                                question.getChapter()
                        ))
                .toList();
    }

    /* ---------- ADVANCED FETCHING ---------- */

    @Override
    public List<QuestionResponseDto> getByChapterId(Long chapterId) {
        return questionRepository.findByChapterIdAndChapterActiveTrue(chapterId)
                .stream()
                .map(question -> mapToDtoWithChapter(question, question.getChapter()))
                .toList();
    }

    @Override
    public List<QuestionResponseDto> getByMultipleChapters(Long paperIds) {
        if ( paperIds== null) return Collections.emptyList();

        List<Long>chapterIds=paperRepository.findChapterIdsByPaperId(paperIds);

        if ( chapterIds.isEmpty()) return Collections.emptyList();

        return questionRepository.findByChapterIdInAndChapterActiveTrue(chapterIds)
                .stream()
                .map(question -> mapToDtoWithChapter(question, question.getChapter()))
                .toList();
    }

    @Override
    public List<QuestionResponseDto> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isBlank()) return Collections.emptyList();

        return questionRepository.searchByKeyword(keyword.trim())
                .stream()
                .map(question -> mapToDtoWithChapter(question, question.getChapter()))
                .toList();
    }

    @Override
    public List<QuestionResponseDto> generateRandomQuiz(
            Long chapterId,
            int limit
    ) {

        List<Long> randomIds =
                questionRepository.getRandomQuestionIds(
                        chapterId,
                        limit
                );

        if (randomIds.isEmpty()) {
            return Collections.emptyList();
        }

        return questionRepository.findByIdIn(randomIds)
                .stream()
                .map(question ->
                        mapToDtoWithChapter(
                                question,
                                question.getChapter()
                        ))
                .toList();
    }

    @Override
    public long countByChapter(Long chapterId) {
        return questionRepository.countByChapterIdAndChapterActiveTrue(chapterId);
    }

    /* ---------- UPDATE ---------- */

    @Override
    @Transactional
    public QuestionResponseDto update(
            Long id,
            QuestionUpdateRequestDto request
    ) {

        Question question =
                questionRepository.findByIdWithChapter(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Question not found with id: " + id
                                ));

        validateCorrectIndexes(
                request.getOptions() != null
                        ? request.getOptions()
                        : question.getOptions(),

                request.getCorrect() != null
                        ? request.getCorrect()
                        : question.getCorrect()
        );

        Chapter chapter = question.getChapter();

        if (request.getChapterId() != null
                && !request.getChapterId().equals(chapter.getId())) {

            chapter = chapterRepository.findById(
                            request.getChapterId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Chapter not found id "
                                            + request.getChapterId()
                            ));

            if (!chapter.getActive()) {
                throw new BadRequestException(
                        "Cannot move question to an inactive chapter."
                );
            }

            question.setChapter(chapter);
        }

        questionMapper.updateEntity(request, question);

        Question updated =
                questionRepository.save(question);

        return mapToDtoWithChapter(
                updated,
                chapter
        );
    }

    /* ---------- DELETE & ADMIN ---------- */

    @Override
    @Transactional
    public void delete(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }
        // Because of @SQLDelete on the entity, this automatically sets active = false
        questionRepository.deleteById(id);
        log.info("Soft-deleted (deactivated) Question ID: {}", id);
    }

    @Override
    public List<QuestionResponseDto> getDeactivatedQuestionsByChapter(Long chapterId) {
        return questionRepository.findDeactivatedQuestionsByChapterId(chapterId)
                .stream()
                .map(question -> mapToDtoWithChapter(question, question.getChapter()))
                .toList();
    }

    @Override
    @Transactional
    public void activate(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        question.setActive(true);
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        question.setActive(false); // Flags it as soft-deleted
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.warn("PERMANENTLY deleting Question ID: {}", id);
        questionRepository.hardDeleteById(id);
    }

    /* ---------- VALIDATION HELPERS ---------- */

    private QuestionResponseDto mapToDtoWithChapter(Question question, Chapter chapter) {
        QuestionResponseDto dto = questionMapper.toDto(question);
        if (chapter != null) {
            dto.setChapterId(chapter.getId());
            dto.setChapterName(chapter.getTitle());
        }
        return dto;
    }

    private void validateCorrectIndexes(
            List<Option> options,
            List<Integer> correctIndexes
    ) {

        if (options == null || options.isEmpty()) {
            throw new BadRequestException(
                    "Options must not be empty"
            );
        }

        if (correctIndexes == null
                || correctIndexes.isEmpty()) {

            throw new BadRequestException(
                    "Correct answer must be provided"
            );
        }

        int size = options.size();

        for (Integer index : correctIndexes) {

            if (index == null
                    || index < 0
                    || index >= size) {

                throw new BadRequestException(
                        "Correct answer index out of range: "
                                + index
                );
            }
        }
    }

}