package getintouch.com.GetInTouch.Service.Chapter;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterRequestDTO;
import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import getintouch.com.GetInTouch.Mapper.ChapterMapper;
import getintouch.com.GetInTouch.Repository.ChapterRepository;
import getintouch.com.GetInTouch.Repository.PaperRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final PaperRepository paperRepository;

    @Override
    public ChapterResponseDTO createChapter(ChapterRequestDTO dto) {
        Paper paper = paperRepository.findById(dto.getPaperId())
                .orElseThrow(() -> new EntityNotFoundException("Paper not found with id: " + dto.getPaperId()));

        if (chapterRepository.existsByPaperIdAndChapterNumber(dto.getPaperId(), dto.getChapterNumber())) {
            throw new IllegalArgumentException("Chapter number already exists");
        }

        Chapter chapter = ChapterMapper.toEntity(dto, paper);
        Chapter savedChapter = chapterRepository.save(chapter);

        return ChapterMapper.toResponseDTO(savedChapter, savedChapter.getPaper());
    }

    @Override
    public ChapterResponseDTO updateChapter(Long id, ChapterRequestDTO dto) {
        // Uses graph-backed fetch plan to cleanly load parent entities alongside Chapter
        Chapter chapter = chapterRepository.findWithPaperById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));

        if (!chapter.getChapterNumber().equals(dto.getChapterNumber())
                && chapterRepository.existsByPaperIdAndChapterNumber(
                chapter.getPaper().getId(), // Eagerly loaded via the graph: No N+1 leak here!
                dto.getChapterNumber())) {
            throw new IllegalArgumentException("Chapter number already exists");
        }

        ChapterMapper.updateEntity(chapter, dto);
        Chapter savedChapter = chapterRepository.save(chapter);

        return ChapterMapper.toResponseDTO(savedChapter, savedChapter.getPaper());
    }

    @Override
    public void deleteChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));

        chapter.setActive(false);
    }

    @Override
    public ChapterResponseDTO activateChapter(Long id) {
        Chapter chapter = chapterRepository.findWithPaperById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));

        chapter.setActive(true);

        return ChapterMapper.toResponseDTO(chapter, chapter.getPaper());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getAllChapters() {
        return chapterRepository.findAll() // Overridden with @EntityGraph inside your ChapterRepository
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterResponseDTO getChapterById(Long id) {
        Chapter chapter = chapterRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));

        return ChapterMapper.toResponseDTO(chapter, chapter.getPaper());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getAllActiveChapters() {
        return chapterRepository.findByActiveTrueOrderByChapterNumberAsc()
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getChaptersByPaperId(Long paperId) {
        return chapterRepository.findByPaperIdOrderByChapterNumberAsc(paperId)
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> getActiveChaptersByPaperId(Long paperId) {
        return chapterRepository.findByPaperIdAndActiveTrueOrderByChapterNumberAsc(paperId)
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterResponseDTO getChapterByPaperIdAndChapterNumber(Long paperId, Long chapterNumber) {
        Chapter chapter = chapterRepository.findByPaperIdAndChapterNumber(paperId, chapterNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Chapter not found for paperId: " + paperId + " and chapterNumber: " + chapterNumber));

        return ChapterMapper.toResponseDTO(chapter, chapter.getPaper());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDTO> searchChapters(String keyword) {
        String search = (keyword == null) ? "" : keyword;

        // Fully database-driven filtering combined with clean relationship fetching
        return chapterRepository.findByActiveTrueAndTitleContainingIgnoreCaseOrderByChapterNumberAsc(search)
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }
}
