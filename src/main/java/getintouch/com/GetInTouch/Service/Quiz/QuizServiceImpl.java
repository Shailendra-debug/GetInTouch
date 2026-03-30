package getintouch.com.GetInTouch.Service.Quiz;


import getintouch.com.GetInTouch.Configuration.SecurityConfig;
import getintouch.com.GetInTouch.DTO.Quiz.QuizRequestDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;

import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Quiz.*;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Filter.JwtAuthFilter;
import getintouch.com.GetInTouch.Mapper.QuizMapper;
import getintouch.com.GetInTouch.Repository.*;
import getintouch.com.GetInTouch.Util.JwtUtil;
import getintouch.com.GetInTouch.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;

    @Override
    public QuizResponseWithQuestionsDTO createQuiz(QuizRequestDTO request) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + request.getCourseId())
                );

        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());

        if (questions.size() != request.getQuestionIds().size()) {
            throw new BadRequestException("Some questions not found");
        }

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .timeLimit(request.getTimeLimit())
                .active(request.isActive())
                .type(request.getType())
                .course(course)
                .passMarks(request.getPassingMarks())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .questions(questions)
                .build();

        return QuizMapper.toWithQuestions(quizRepository.save(quiz));
    }

    @Override
    public List<QuizResponseWithoutQuestionsDTO> getAllActiveQuizzes() {

        return quizRepository.findByActiveTrue()
                .stream()
                .map(QuizMapper::toWithoutQuestions)
                .toList();
    }

    @Override
    public QuizResponseWithoutQuestionsDTO getQuizSummary(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Quiz not found with id: " + quizId)
                );

        return QuizMapper.toWithoutQuestions(quiz);
    }

    @Override
    public QuizResponseWithQuestionsDTO getQuizWithQuestions(Long quizId) {

        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Quiz not found with id: " + quizId)
                );

        return QuizMapper.toWithQuestions(quiz);
    }

    @Override
    public  QuizResponseWithQuestionsDTO startQuiz(Long quizId) {

        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Quiz not found with id: " + quizId)
                );

        if (!quiz.isActive()) {
            throw new BadRequestException("Quiz is not active");
        }

        if (quiz.getType() == QuizType.LIVE) {

            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(quiz.getStartTime())) {
                throw new BadRequestException("Quiz has not started yet");
            }

            if (now.isAfter(quiz.getEndTime())) {
                throw new BadRequestException("Quiz has already ended");
            }
        }

        QuizAttempt attempt=new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(userRepository.getReferenceById(Objects.requireNonNull(SecurityUtil.getCurrentUserId())));
        attempt.setStatus(ResultStatus.FAIL);
        QuizAttempt setrtAttempt=quizAttemptRepository.save(attempt);

        QuizResponseWithQuestionsDTO quizResponseWithQuestionsDTO=QuizMapper.toWithQuestions(quiz);
        quizResponseWithQuestionsDTO.setAttemptId(setrtAttempt.getId());
        return quizResponseWithQuestionsDTO;
    }

    @Override
    public QuizResponseWithQuestionsDTO updateQuiz(Long quizId, QuizRequestDTO request) {

        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Quiz not found with id: " + quizId)
                );

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + request.getCourseId())
                );

        List<Question> questions = questionRepository.findAllById(request.getQuestionIds());

        if (questions.size() != request.getQuestionIds().size()) {
            throw new BadRequestException("Some questions not found");
        }

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setActive(request.isActive());
        quiz.setType(request.getType());
        quiz.setCourse(course);
        quiz.setPassMarks(request.getPassingMarks());
        quiz.setStartTime(request.getStartTime());
        quiz.setEndTime(request.getEndTime());
        quiz.setQuestions(questions);

        return QuizMapper.toWithQuestions(quizRepository.save(quiz));
    }

    @Override
    public void deleteQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Quiz not found with id: " + quizId)
                );

        quizRepository.delete(quiz);
    }
}
