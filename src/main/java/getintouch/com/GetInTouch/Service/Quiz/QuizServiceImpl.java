package getintouch.com.GetInTouch.Service.Quiz;

import getintouch.com.GetInTouch.DTO.Quiz.QuizRequestDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import getintouch.com.GetInTouch.Entity.Quiz.Quiz;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttempt;
import getintouch.com.GetInTouch.Entity.Quiz.QuizType;
import getintouch.com.GetInTouch.Entity.Quiz.ResultStatus;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.QuizMapper;
import getintouch.com.GetInTouch.Repository.*;
import getintouch.com.GetInTouch.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // Optimize global reads
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    // SecurityConfig removed from DI as SecurityUtil is typically accessed statically

    /* =========================================================================
       WRITE OPERATIONS
       ========================================================================= */

    @Override
    @Transactional
    public QuizResponseWithQuestionsDTO createQuiz(QuizRequestDTO request) {
        log.info("Creating {} quiz for Chapter ID: {}", request.getType(), request.getChapterId());

        // 1. Fetch & Validate Chapter
        Chapter chapter = chapterRepository.findByIdWithPaper(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + request.getChapterId()));

        if (quizRepository.existsByChapterIdAndTitleIgnoreCase(chapter.getId(), request.getTitle())) {
            throw new BadRequestException("A quiz with this title already exists in this chapter.");
        }

        // 2. Fetch & Validate Questions
        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.size() != request.getQuestionIds().size()) {
            throw new BadRequestException("Some questions could not be found. Please verify the IDs.");
        }

        // 🔥 CALCULATE TOTAL MARKS DYNAMICALLY FOR VALIDATION
        int calculatedTotalMarks = questions.stream().mapToInt(Question::getMarks).sum();

        validateQuizSchedule(request.getType(), request.getStartTime(), request.getEndTime());
        validateMarks(request.getPassingMarks(), calculatedTotalMarks); // Use the calculated sum here!

        // 3. Build & Save Quiz
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .timeLimit(request.getTimeLimit())
                .active(request.isActive())
                .thumbnail(request.getThumbnail())
                .type(request.getType())
                .chapter(chapter)
                .passMarks(request.getPassingMarks())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .questions(questions)
                .build();

        Quiz saved = quizRepository.save(quiz);
        return QuizMapper.toWithQuestions(saved, chapter);
    }

    @Override
    @Transactional
    public QuizResponseWithQuestionsDTO updateQuiz(Long quizId, QuizRequestDTO request) {
        log.info("Updating Quiz ID: {}", quizId);

        Quiz quiz = quizRepository.findCompleteQuizById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        Chapter chapter = quiz.getChapter();

        // 1. Validate Title Clashes
        if (!quiz.getTitle().equalsIgnoreCase(request.getTitle()) &&
                quizRepository.existsByChapterIdAndTitleIgnoreCase(chapter.getId(), request.getTitle())) {
            throw new BadRequestException("A quiz with this title already exists in this chapter.");
        }

        // 2. Handle Chapter transfer
        if (request.getChapterId() != null && !request.getChapterId().equals(chapter.getId())) {
            chapter = chapterRepository.findByIdWithPaper(request.getChapterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target chapter not found: " + request.getChapterId()));
            quiz.setChapter(chapter);
        }

        // 3. Fetch & Validate Questions
        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.size() != request.getQuestionIds().size()) {
            throw new BadRequestException("Some questions could not be found.");
        }

        // 🔥 CALCULATE TOTAL MARKS DYNAMICALLY FOR VALIDATION
        int calculatedTotalMarks = questions.stream().mapToInt(Question::getMarks).sum();

        validateQuizSchedule(request.getType(), request.getStartTime(), request.getEndTime());
        validateMarks(request.getPassingMarks(), calculatedTotalMarks); // Use the calculated sum here!

        // 4. Update Properties
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setActive(request.isActive());
        quiz.setType(request.getType());
        quiz.setPassMarks(request.getPassingMarks());
        quiz.setStartTime(request.getStartTime());
        quiz.setEndTime(request.getEndTime());
        quiz.setQuestions(questions);
        quiz.setThumbnail(request.getThumbnail());

        Quiz updated = quizRepository.save(quiz);
        return QuizMapper.toWithQuestions(updated, chapter);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with id: " + quizId);
        }
        // Triggers @SQLDelete for soft deletion
        quizRepository.deleteById(quizId);
    }

    /* =========================================================================
       READ OPERATIONS (Excluding Attempt Logic)
       ========================================================================= */

    @Override
    public List<QuizResponseWithoutQuestionsDTO> getAllQuizzes() {
        return quizRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(quiz -> QuizMapper.toWithoutQuestions(quiz, quiz.getChapter()))
                .toList();
    }

    @Override
    public List<QuizResponseWithoutQuestionsDTO> getAllActiveQuizzes() {
        return getAllQuizzes(); // Handled automatically by @SQLRestriction
    }

    @Override
    public QuizResponseWithoutQuestionsDTO getQuizSummary(Long quizId) {
        Quiz quiz = quizRepository.findByIdWithChapter(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        return QuizMapper.toWithoutQuestions(quiz, quiz.getChapter());
    }

    @Override
    public List<QuizResponseWithoutQuestionsDTO> getGeneralQuizzesByPaper(Long paperId) {
        return quizRepository.findByPaperIdAndChapterIsNullOrderByCreatedAtDesc(paperId)
                .stream()
                .map(quiz -> QuizMapper.toWithoutQuestions(quiz, quiz.getChapter()))
                .toList();
    }

    @Override
    public QuizResponseWithQuestionsDTO getQuizWithQuestions(Long quizId) {
        Quiz quiz = quizRepository.findCompleteQuizById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        return QuizMapper.toWithQuestions(quiz, quiz.getChapter());
    }

    /* =========================================================================
       STUDENT TEST TAKING (Attempt Logic)
       ========================================================================= */

    @Override
    @Transactional // Must be transactional to save the QuizAttempt
    public QuizResponseWithQuestionsDTO startQuiz(Long quizId) {
        log.info("Student starting Quiz ID: {}", quizId);

        Quiz quiz = quizRepository.findCompleteQuizById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Note: No need for !quiz.isActive() check due to @SQLRestriction

        // 1. Validate Schedule for LIVE/EXAM quizzes
        if (quiz.getType() == QuizType.LIVE || quiz.getType() == QuizType.EXAM) {
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            if (now.isBefore(quiz.getStartTime())) {
                throw new BadRequestException("This quiz has not started yet.");
            }
            if (now.isAfter(quiz.getEndTime())) {
                throw new BadRequestException("This quiz has already ended.");
            }
        }

        // 2. Initialize Attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(userRepository.getReferenceById(
                Objects.requireNonNull(SecurityUtil.getCurrentUserId())
        ));
        attempt.setStartTime(OffsetDateTime.now(ZoneOffset.UTC));
        attempt.setStatus(ResultStatus.FAIL);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        // 3. Attach Attempt ID to Response
        QuizResponseWithQuestionsDTO response = QuizMapper.toWithQuestions(quiz, quiz.getChapter());
        response.setAttemptId(savedAttempt.getId());

        return response;
    }

    /* =========================================================================
       ADVANCED FETCHING
       ========================================================================= */

    @Override
    public List<QuizResponseWithoutQuestionsDTO> getQuizzesByChapter(Long chapterId) {
        return quizRepository.findByChapterIdOrderByCreatedAtDesc(chapterId)
                .stream()
                .map(quiz -> QuizMapper.toWithoutQuestions(quiz, quiz.getChapter()))
                .toList();
    }

    @Override
    public List<QuizResponseWithoutQuestionsDTO> getCurrentlyRunningQuizzes() {
        return quizRepository.findCurrentlyRunningQuizzes()
                .stream()
                .map(quiz -> QuizMapper.toWithoutQuestions(quiz, quiz.getChapter()))
                .toList();
    }

    /* =========================================================================
       ADMIN TRASH BIN
       ========================================================================= */

    @Override
    public List<QuizResponseWithoutQuestionsDTO> getDeactivatedQuizzes(Long chapterId) {

        // 1. Fetch the chapter exactly ONCE upfront
        Chapter chapter = chapterRepository.findByIdWithPaper(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + chapterId));

        // 2. Fetch the native query results
        return quizRepository.findDeactivatedQuizzesByChapterId(chapterId)
                .stream()
                // 3. Pass our pre-fetched chapter directly. NO N+1 QUERIES!
                .map(quiz -> QuizMapper.toWithoutQuestions(quiz, chapter))
                .toList();
    }

    @Override
    @Transactional
    public void activateQuiz(Long quizId) {
        log.info("Reactivating Quiz ID: {}", quizId);
        quizRepository.activateById(quizId);
    }

    /* =========================================================================
       VALIDATION HELPERS
       ========================================================================= */

    private void validateQuizSchedule(QuizType type, OffsetDateTime start, OffsetDateTime end) {
        if (type == QuizType.LIVE || type == QuizType.EXAM) {
            if (start == null || end == null) {
                throw new BadRequestException("LIVE and EXAM quizzes must have a valid Start Time and End Time.");
            }
            if (start.isAfter(end)) {
                throw new BadRequestException("Quiz Start Time cannot be after End Time.");
            }
        } else if (type == QuizType.PRACTICE) {
            if (start != null || end != null) {
                throw new BadRequestException("PRACTICE quizzes must not have scheduled Start or End times.");
            }
        }
    }

    private void validateMarks(int passMarks, int totalMarks) {
        if (passMarks < 0 || totalMarks < 0) {
            throw new BadRequestException("Marks cannot be negative.");
        }
        if (passMarks > totalMarks) {
            throw new BadRequestException("Pass marks cannot be greater than Total marks.");
        }
    }
}