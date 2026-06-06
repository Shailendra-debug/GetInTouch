package getintouch.com.GetInTouch.Service.Paper;


import getintouch.com.GetInTouch.DTO.Paper.PaperRequestDTO;
import getintouch.com.GetInTouch.DTO.Paper.PaperResponseDTO;

import java.util.List;

public interface PaperService {

    // ================= ADMIN =================

    PaperResponseDTO createPaper(
            PaperRequestDTO requestDTO
    );

    PaperResponseDTO updatePaper(
            Long id,
            PaperRequestDTO requestDTO
    );

    void deletePaper(
            Long id
    );

    PaperResponseDTO activatePaper(
            Long id
    );

    List<PaperResponseDTO> getAllPapers();

    // ================= USER =================

    PaperResponseDTO getPaperById(
            Long id
    );

    List<PaperResponseDTO> getAllActivePapers();

    List<PaperResponseDTO> getPapersByCourseId(
            Long courseId
    );

    List<PaperResponseDTO> getActivePapersByCourseId(
            Long courseId
    );

    PaperResponseDTO getPaperByCourseIdAndPaperNumber(
            Long courseId,
            Long paperNumber
    );

    List<PaperResponseDTO> searchPapers(
            String keyword
    );
}
