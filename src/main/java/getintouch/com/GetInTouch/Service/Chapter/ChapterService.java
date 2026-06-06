package getintouch.com.GetInTouch.Service.Chapter;

import getintouch.com.GetInTouch.DTO.Chapter.ChapterRequestDTO;
import getintouch.com.GetInTouch.DTO.Chapter.ChapterResponseDTO;

import java.util.List;

public interface ChapterService {

    /**
     * Creates a new chapter under a specific paper.
     * @param dto the chapter details
     * @return the created chapter DTO
     */
    ChapterResponseDTO createChapter(ChapterRequestDTO dto);

    /**
     * Updates an existing chapter's details.
     * @param id the chapter ID to update
     * @param dto the new chapter details
     * @return the updated chapter DTO
     */
    ChapterResponseDTO updateChapter(Long id, ChapterRequestDTO dto);

    /**
     * Soft deletes a chapter by setting its active status to false.
     * @param id the chapter ID to delete
     */
    void deleteChapter(Long id);

    /**
     * Re-activates a soft-deleted chapter.
     * @param id the chapter ID to activate
     * @return the activated chapter DTO
     */
    ChapterResponseDTO activateChapter(Long id);

    /**
     * Retrieves all chapters in the system (including inactive ones).
     * Used mainly for administrative dashboards.
     */
    List<ChapterResponseDTO> getAllChapters();

    /**
     * Retrieves a single active chapter by its ID.
     */
    ChapterResponseDTO getChapterById(Long id);

    /**
     * Retrieves all globally active chapters across all papers.
     */
    List<ChapterResponseDTO> getAllActiveChapters();

    /**
     * Retrieves all chapters belonging to a specific paper.
     */
    List<ChapterResponseDTO> getChaptersByPaperId(Long paperId);

    /**
     * Retrieves only the active chapters belonging to a specific paper.
     */
    List<ChapterResponseDTO> getActiveChaptersByPaperId(Long paperId);

    /**
     * Retrieves a specific chapter using its paper context and chapter number constraint.
     */
    ChapterResponseDTO getChapterByPaperIdAndChapterNumber(Long paperId, Long chapterNumber);

    /**
     * Performs a database-level case-insensitive keyword search on active chapter titles.
     */
    List<ChapterResponseDTO> searchChapters(String keyword);
}