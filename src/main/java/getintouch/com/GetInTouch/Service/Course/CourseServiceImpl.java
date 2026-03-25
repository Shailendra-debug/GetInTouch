package getintouch.com.GetInTouch.Service.Course;

import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Entity.Quiz.Course;
import getintouch.com.GetInTouch.Exception.BadRequestException;
import getintouch.com.GetInTouch.Exception.ResourceNotFoundException;
import getintouch.com.GetInTouch.Mapper.CourseMapper;
import getintouch.com.GetInTouch.Repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO request) {

        if (courseRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Course already exists");
        }

        Course course = CourseMapper.toEntity(request);
        return CourseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {

        return courseRepository.findAll()
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Override
    public CourseResponseDTO getCourseById(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        return CourseMapper.toResponse(course);
    }

    @Override
    public CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO request) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        course.setName(request.getName());
        return CourseMapper.toResponse(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        courseRepository.delete(course);
    }
}

