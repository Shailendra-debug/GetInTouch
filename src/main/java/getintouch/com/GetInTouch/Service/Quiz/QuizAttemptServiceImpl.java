package getintouch.com.GetInTouch.Service.Quiz;

import getintouch.com.GetInTouch.DTO.Quiz.*;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttempt;
import getintouch.com.GetInTouch.Entity.Quiz.QuizAttemptAnswer;
import getintouch.com.GetInTouch.Entity.Quiz.QuizType;
import getintouch.com.GetInTouch.Entity.Quiz.ResultStatus;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.QuizAttemptMapper;
import getintouch.com.GetInTouch.Repository.QuizAttemptAnswerRepository;
import getintouch.com.GetInTouch.Repository.QuizAttemptRepository;
import getintouch.com.GetInTouch.Repository.QuizRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAttemptAnswerRepository answerRepository;
    private final QuizAttemptMapper mapper;

    /* ================= SUBMIT QUIZ ================= */

    @Override
    public QuizSubmitResponseDto submitQuiz(QuizSubmitRequestDto request) {

        if (request.getAnswers()==null) {
            throw new BadRequestException("Answers cannot be empty");
        }

        QuizAttempt attempt = attemptRepository.findById(request.getAttemptId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attempt not found"));

        if (attempt.isSubmitted()&&attempt.getQuiz().getType()== QuizType.LIVE) {
            throw new BadRequestException("Quiz already submitted");
        }

        Map<Long, QuestionAnswerDto> answerMap =
                request.getAnswers()
                        .stream()
                        .collect(Collectors.toMap(
                                QuestionAnswerDto::getQuestionId,
                                a -> a,
                                (a, b) -> {
                                    throw new BadRequestException(
                                            "Duplicate answers for same question"
                                    );
                                }
                        ));


        int score = 0;
        int correct = 0;
        int attempted = 0;

        List<QuestionResultDto> results = new ArrayList<>();

        for (Question q : attempt.getQuiz().getQuestions()) {

            QuestionAnswerDto ans = answerMap.get(q.getId());
            if (ans == null) continue;

            attempted++;

            boolean isCorrect =
                    new HashSet<>(q.getCorrect())
                            .equals(new HashSet<>(ans.getSelectedIndexes()));

            int marks = isCorrect ? q.getMarks() : 0;

            if (isCorrect) {
                score += marks;
                correct++;
            }

            QuizAttemptAnswer answer =
                    QuizAttemptAnswer.builder()
                            .attempt(attempt)
                            .question(q)
                            .selectedIndexes(new ArrayList<>(ans.getSelectedIndexes()))
                            .correctIndexes(new ArrayList<>(q.getCorrect()))
                            .correct(isCorrect)
                            .marksObtained(marks)
                            .build();

            answerRepository.save(answer);
            results.add(mapper.toQuestionResult(answer));
        }

        attempt.setAttemptedQuestions(attempted);
        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(attempted - correct);
        attempt.setScore(score);
        attempt.setTotalMarks(attempt.getQuiz().getTotalMarks());

        double percentage = attempt.getTotalMarks() == 0
                ? 0.0
                : (score * 100.0) / attempt.getTotalMarks();

        attempt.setPercentage(percentage);
        attempt.setStatus(
                percentage >= attempt.getQuiz().getPassMarks()
                        ? ResultStatus.PASS
                        : ResultStatus.FAIL
        );

        attempt.setSubmitted(true);
        attempt.setEndTime(LocalDateTime.now());

        attemptRepository.save(attempt);

        return mapper.toResponse(attempt, results);
    }

    /* ================= GET BY ATTEMPT ID ================= */

    @Override
    @Transactional
    public QuizSubmitResponseDto getByAttemptId(Long attemptId) {

        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attempt not found"));

        List<QuestionResultDto> results =
                answerRepository.findByAttemptId(attemptId)
                        .stream()
                        .map(mapper::toQuestionResult)
                        .toList();

        return mapper.toResponse(attempt, results);
    }

    /* ================= GET ALL BY USER ================= */

    @Override
    @Transactional
    public List<QuizSubmitResponseDto> getAllByUserId(Long userId) {

        return attemptRepository.findByUserId(userId)
                .stream()
                .map(attempt -> {
                    List<QuestionResultDto> results =
                            answerRepository.findByAttemptId(attempt.getId())
                                    .stream()
                                    .map(mapper::toQuestionResult)
                                    .toList();
                    return mapper.toResponse(attempt, results);
                })
                .toList();
    }

    @Override
    public List<QuizSubmitByQuizIdDtu> getAllByQuizId(Long quizId) {
        if (quizRepository.existsById(quizId)){
            return attemptRepository.findByQuizId(quizId).stream().map(this::setAll).toList();
        }
        return null;
    }
    private QuizSubmitByQuizIdDtu setAll(QuizAttempt attempt){
        User user=attempt.getUser();
        return QuizSubmitByQuizIdDtu.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .attemptId(attempt.getId())
                .totalScore(attempt.getScore())
                .status(attempt.getStatus())
                .build();
    }
}


