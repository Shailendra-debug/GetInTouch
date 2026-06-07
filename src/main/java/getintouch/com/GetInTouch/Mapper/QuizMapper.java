package getintouch.com.GetInTouch.Mapper;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import getintouch.com.GetInTouch.DTO.Question.QuestionResponseForQuizDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithQuestionsDTO;
import getintouch.com.GetInTouch.DTO.Quiz.QuizResponseWithoutQuestionsDTO;
import getintouch.com.GetInTouch.Entity.Question.Question;
import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import getintouch.com.GetInTouch.Entity.Quiz.Quiz;

import java.time.ZoneId;
import java.util.List;

public class QuizMapper {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private QuizMapper() {
        // utility class
    }

    /* =====================================================
       QUIZ → RESPONSE (WITHOUT QUESTIONS)
       ===================================================== */
    public static QuizResponseWithoutQuestionsDTO toWithoutQuestions(Quiz quiz, Chapter chapter) {

        return QuizResponseWithoutQuestionsDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .active(quiz.isActive())
                .showResult(quiz.getShowResult())
                .type(quiz.getType() != null ? quiz.getType().name() : null)
                .chapter(mapChapter(chapter)) // <-- Passes the full mapped object
                .passingMarks(quiz.getPassMarks())
                .totalMarks(quiz.getTotalMarks())
                .startTime(quiz.getStartTime() != null ? quiz.getStartTime().atZoneSameInstant(IST) : null)
                .endTime(quiz.getEndTime() != null ? quiz.getEndTime().atZoneSameInstant(IST) : null)
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    /* =====================================================
       QUIZ → RESPONSE (WITH QUESTIONS)
       ===================================================== */
    public static QuizResponseWithQuestionsDTO toWithQuestions(Quiz quiz, Chapter chapter) {

        List<QuestionResponseForQuizDTO> questionDTOs =
                (quiz.getQuestions() != null)
                        ? quiz.getQuestions().stream().map(QuizMapper::mapQuestionForQuiz).toList()
                        : List.of();

        return QuizResponseWithQuestionsDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .active(quiz.isActive())
                .showResult(quiz.getShowResult())
                .type(quiz.getType() != null ? quiz.getType().name() : null)
                .chapter(mapChapter(chapter)) // <-- Passes the full mapped object
                .questions(questionDTOs)
                .totalQuestions(questionDTOs.size())
                .totalMarks(quiz.getTotalMarks())
                .passingMarks(quiz.getPassMarks())
                .startTime(quiz.getStartTime())
                .endTime(quiz.getEndTime())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    /* =====================================================
       HELPERS
       ===================================================== */

    private static ChapterResponseDTO mapChapter(Chapter chapter) {
        if (chapter == null) return null;

        // Maps the core Chapter details. (Adjust fields if your ChapterResponseDTO requires more)
        return ChapterResponseDTO.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .description(chapter.getDescription())
                .thumbnail(chapter.getThumbnail())
                .active(chapter.getActive())
                .build();
    }

    private static QuestionResponseForQuizDTO mapQuestionForQuiz(Question q) {
        return QuestionResponseForQuizDTO.builder()
                .id(q.getId())
                .question(q.getQuestion())
                .options(q.getOptions())
                .type(q.getType())
                .difficulty(q.getDifficulty())
                .marks(q.getMarks())
                .build();
    }
}