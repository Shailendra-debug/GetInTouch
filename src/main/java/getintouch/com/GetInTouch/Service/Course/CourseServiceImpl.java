package getintouch.com.GetInTouch.Service.Course;

import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Course;
import getintouch.com.GetInTouch.Mapper.CourseMapper;
import getintouch.com.GetInTouch.Repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    // ===================== ADMIN =====================

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {

        if (courseRepository.existsByNameIgnoreCase(requestDTO.getName())) {
            throw new RuntimeException("Course already exists.");
        }

        if (courseRepository.existsByCourseNumber(requestDTO.getCourseNumber())) {
            throw new RuntimeException("Course number already exists.");
        }

        Course course = CourseMapper.toEntity(requestDTO);

        course = courseRepository.save(course);

        return CourseMapper.toResponseDTO(course);
    }

    @Override
    public CourseResponseDTO updateCourse(
            Long id,
            CourseRequestDTO requestDTO
    ) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found."));

        CourseMapper.updateEntity(course, requestDTO);

        course = courseRepository.save(course);

        return CourseMapper.toResponseDTO(course);
    }

    @Override
    public void deleteCourse(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found."));

        course.setActive(false);

        courseRepository.save(course);
    }

    @Override
    public CourseResponseDTO activateCourse(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found."));

        course.setActive(true);

        course = courseRepository.save(course);

        return CourseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional()
    public List<CourseResponseDTO> getAllCourses() {

        return courseRepository.findAllByOrderByCourseNumberAsc()
                .stream()
                .map(CourseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ===================== USER =====================

    @Override
    @Transactional()
    public List<CourseResponseDTO> getAllActiveCourses() {

        return courseRepository.findByActiveTrueOrderByCourseNumberAsc()
                .stream()
                .map(CourseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional()
    public CourseResponseDTO getCourseById(Long id) {

        Course course = courseRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Course not found."));

        return CourseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional()
    public CourseResponseDTO getCourseByCourseNumber(
            Long courseNumber
    ) {

        Course course = courseRepository
                .findByCourseNumberAndActiveTrue(courseNumber)
                .orElseThrow(() -> new RuntimeException("Course not found."));

        return CourseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional()
    public List<CourseResponseDTO> searchCourses(
            String keyword
    ) {

        return courseRepository.findByActiveTrueOrderByCourseNumberAsc()
                .stream()
                .filter(course ->
                        course.getName().toLowerCase()
                                .contains(keyword.toLowerCase()))
                .map(CourseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}

