package getintouch.com.GetInTouch.Service.Chapter;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterRequestDTO;
import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Chapter;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.ChapterMapper;
import getintouch.com.GetInTouch.Repository.ChapterRepository;
import getintouch.com.GetInTouch.Repository.PaperRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Added for production logging
@Transactional(readOnly = true) // Optimize read operations globally
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final PaperRepository paperRepository;

    // =========================================================================
    // WRITE OPERATIONS (Transactional)
    // =========================================================================

    @Override
    @Transactional
    public ChapterResponseDTO createChapter(ChapterRequestDTO dto) {
        log.info("Creating new chapter for Paper ID: {}", dto.getPaperId());

        Paper paper = paperRepository.findById(dto.getPaperId())
                .orElseThrow(() -> new EntityNotFoundException("Paper not found with id: " + dto.getPaperId()));

        if (chapterRepository.existsByPaperIdAndChapterNumber(dto.getPaperId(), dto.getChapterNumber())) {
            throw new IllegalArgumentException("Chapter number already exists in this paper");
        }

        if (chapterRepository.existsByPaperIdAndTitleIgnoreCase(dto.getPaperId(), dto.getTitle())) {
            throw new IllegalArgumentException("Chapter title already exists in this paper");
        }

        Chapter chapter = ChapterMapper.toEntity(dto, paper);
        chapter.setActive(true); // Ensure new chapters default to active

        Chapter savedChapter = chapterRepository.save(chapter);
        return ChapterMapper.toResponseDTO(savedChapter, savedChapter.getPaper());
    }

    @Override
    @Transactional
    public ChapterResponseDTO updateChapter(Long id, ChapterRequestDTO dto) {
        log.info("Updating Chapter ID: {}", id);

        // Updated to the new N+1 safe repository method name
        Chapter chapter = chapterRepository.findByIdWithPaper(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));

        // Eagerly loaded via the graph: No N+1 leak here!
        Long paperId = chapter.getPaper().getId();

        if (!chapter.getChapterNumber().equals(dto.getChapterNumber()) &&
                chapterRepository.existsByPaperIdAndChapterNumber(paperId, dto.getChapterNumber())) {
            throw new IllegalArgumentException("Chapter number already exists in this paper");
        }

        if (!chapter.getTitle().equalsIgnoreCase(dto.getTitle()) &&
                chapterRepository.existsByPaperIdAndTitleIgnoreCase(paperId, dto.getTitle())) {
            throw new IllegalArgumentException("Chapter title already exists in this paper");
        }

        ChapterMapper.updateEntity(chapter, dto);
        Chapter savedChapter = chapterRepository.save(chapter);

        return ChapterMapper.toResponseDTO(savedChapter, savedChapter.getPaper());
    }

    @Override
    @Transactional
    public void deleteChapter(Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new EntityNotFoundException("Chapter not found with id: " + id);
        }

        // Because of @SQLDelete on the Entity, this executes an UPDATE active = false
        log.info("Soft-deleting Chapter ID: {}", id);
        chapterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ChapterResponseDTO activateChapter(Long id) {
        log.info("Reactivating Chapter ID: {}", id);

        // 1. Reactivate using the Native SQL query (bypasses @SQLRestriction)
        chapterRepository.activateById(id);

        // 2. Now that it is active, standard fetch works perfectly to return the DTO
        Chapter chapter = chapterRepository.findByIdWithPaper(id)
                .orElseThrow(() -> new EntityNotFoundException("Failed to retrieve chapter after activation."));

        return ChapterMapper.toResponseDTO(chapter, chapter.getPaper());
    }

    @Override
    @Transactional
    public ChapterResponseDTO deactivateChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + id));

        chapter.setActive(false); // Or your specific status logic
        Chapter updatedChapter = chapterRepository.save(chapter);

        return ChapterMapper.toResponseDTO(updatedChapter,updatedChapter.getPaper()); // Assuming you use a mapper
    }

    // =========================================================================
    // READ OPERATIONS (Read-Only)
    // =========================================================================

    @Override
    public List<ChapterResponseDTO> getAllChapters() {
        return chapterRepository.findAllByOrderByChapterNumberAsc()
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }

    @Override
    public ChapterResponseDTO getChapterById(Long id) {
        // Changed to use the optimized graph fetch method
        Chapter chapter = chapterRepository.findByIdWithPaper(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));

        return ChapterMapper.toResponseDTO(chapter, chapter.getPaper());
    }

    public List<ChapterResponseDTO> getAllActiveChapters() {
        // Fetches active chapters + papers sorted by chapter number in 1 query
        List<Chapter> activeChapters = chapterRepository.findByActiveTrueOrderByChapterNumberAsc();

        return activeChapters.stream()
                .map(e -> ChapterMapper.toResponseDTO(e, e.getPaper()))
                .collect(Collectors.toList());
    }


    @Override
    public List<ChapterResponseDTO> getAllInactiveChapters() {
        List<Chapter> inactiveChapters = chapterRepository.findAllDeactivatedChapters(); // Or your equivalent repository method
        return inactiveChapters.stream()
                .map(e->ChapterMapper.toResponseDTO(e,e.getPaper()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChapterResponseDTO> getChaptersByPaperId(Long paperId) {
        return chapterRepository.findByPaperIdOrderByChapterNumberAsc(paperId)
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }

    @Override
    public List<ChapterResponseDTO> getActiveChaptersByPaperId(Long paperId) {
        // Same logic here; inactive chapters are naturally hidden
        return getChaptersByPaperId(paperId);
    }

    @Override
    public ChapterResponseDTO getChapterByPaperIdAndChapterNumber(Long paperId, Long chapterNumber) {
        Chapter chapter = chapterRepository.findByPaperIdAndChapterNumber(paperId, chapterNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Chapter not found for paperId: " + paperId + " and chapterNumber: " + chapterNumber));

        return ChapterMapper.toResponseDTO(chapter, chapter.getPaper());
    }

    @Override
    public List<ChapterResponseDTO> searchChapters(String keyword) {
        String search = (keyword == null || keyword.trim().isBlank()) ? "" : keyword.trim();

        // Updated to the cleaner repository method name
        return chapterRepository.findByTitleContainingIgnoreCaseOrderByChapterNumberAsc(search)
                .stream()
                .map(chapter -> ChapterMapper.toResponseDTO(chapter, chapter.getPaper()))
                .toList();
    }
}