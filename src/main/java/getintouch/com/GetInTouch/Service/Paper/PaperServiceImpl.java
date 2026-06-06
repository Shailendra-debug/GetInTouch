package getintouch.com.GetInTouch.Service.Paper;

import getintouch.com.GetInTouch.DTO.Paper.PaperRequestDTO;
import getintouch.com.GetInTouch.DTO.Paper.PaperResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Course;
import getintouch.com.GetInTouch.Entity.Quiz.Paper;
import getintouch.com.GetInTouch.Mapper.PaperMapper;
import getintouch.com.GetInTouch.Repository.CourseRepository;
import getintouch.com.GetInTouch.Repository.PaperRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;
    private final CourseRepository courseRepository;

    @Override
    public PaperResponseDTO createPaper(PaperRequestDTO dto) {

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found with id: " + dto.getCourseId()));

        if (paperRepository.existsByCourseIdAndPaperNumber(
                dto.getCourseId(),
                dto.getPaperNumber())) {
            throw new IllegalArgumentException(
                    "Paper number already exists");
        }

        Paper paper = PaperMapper.toEntity(dto, course);
        Paper savedPaper = paperRepository.save(paper);

        return PaperMapper.toResponseDTO(
                savedPaper,
                savedPaper.getCourse()
        );
    }

    @Override
    public PaperResponseDTO updatePaper(Long id, PaperRequestDTO dto) {

        Paper paper = paperRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Paper not found with id: " + id));

        if (!paper.getPaperNumber().equals(dto.getPaperNumber())
                && paperRepository.existsByCourseIdAndPaperNumber(
                paper.getCourse().getId(),
                dto.getPaperNumber())) {

            throw new IllegalArgumentException(
                    "Paper number already exists");
        }

        PaperMapper.updateEntity(paper, dto);

        Paper savedPaper = paperRepository.save(paper);

        return PaperMapper.toResponseDTO(
                savedPaper,
                savedPaper.getCourse()
        );
    }

    @Override
    public void deletePaper(Long id) {

        Paper paper = paperRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Paper not found with id: " + id));

        paper.setActive(false);
    }

    @Override
    public PaperResponseDTO activatePaper(Long id) {

        Paper paper = paperRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Paper not found with id: " + id));

        paper.setActive(true);

        return PaperMapper.toResponseDTO(
                paper,
                paper.getCourse()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponseDTO> getAllPapers() {

        return paperRepository.findAll()
                .stream()
                .map(p -> PaperMapper.toResponseDTO(
                        p,
                        p.getCourse()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaperResponseDTO getPaperById(Long id) {

        Paper paper = paperRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Paper not found with id: " + id));

        return PaperMapper.toResponseDTO(
                paper,
                paper.getCourse()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponseDTO> getAllActivePapers() {

        return paperRepository.findByActiveTrueOrderByPaperNumberAsc()
                .stream()
                .map(p -> PaperMapper.toResponseDTO(
                        p,
                        p.getCourse()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponseDTO> getPapersByCourseId(Long courseId) {

        return paperRepository.findByCourseIdOrderByPaperNumberAsc(courseId)
                .stream()
                .map(p -> PaperMapper.toResponseDTO(
                        p,
                        p.getCourse()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponseDTO> getActivePapersByCourseId(Long courseId) {

        return paperRepository
                .findByCourseIdAndActiveTrueOrderByPaperNumberAsc(courseId)
                .stream()
                .map(p -> PaperMapper.toResponseDTO(
                        p,
                        p.getCourse()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaperResponseDTO getPaperByCourseIdAndPaperNumber(
            Long courseId,
            Long paperNumber) {

        Paper paper = paperRepository
                .findByCourseIdAndPaperNumber(courseId, paperNumber)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Paper not found for courseId: "
                                        + courseId
                                        + " and paperNumber: "
                                        + paperNumber));

        return PaperMapper.toResponseDTO(
                paper,
                paper.getCourse()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperResponseDTO> searchPapers(String keyword) {

        String search = keyword == null ? "" : keyword;

        return paperRepository
                .findByActiveTrueAndNameContainingIgnoreCaseOrderByPaperNumberAsc(search)
                .stream()
                .map(p -> PaperMapper.toResponseDTO(
                        p,
                        p.getCourse()))
                .toList();
    }
}
