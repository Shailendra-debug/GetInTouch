package getintouch.com.GetInTouch.Controller;


import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Service.Course.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /* =====================================================
       CREATE COURSE (ADMIN)
       ===================================================== */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(
            @Valid @RequestBody CourseRequestDTO request
    ) {
        return new ResponseEntity<>(
                courseService.createCourse(request),
                HttpStatus.CREATED
        );
    }

    /* =====================================================
       GET ALL COURSES (USER + ADMIN)
       ===================================================== */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /* =====================================================
       GET COURSE BY ID
       ===================================================== */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> getCourseById(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    /* =====================================================
       UPDATE COURSE (ADMIN)
       ===================================================== */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequestDTO request
    ) {
        return ResponseEntity.ok(
                courseService.updateCourse(courseId, request)
        );
    }

    /* =====================================================
       DELETE COURSE (ADMIN)
       ===================================================== */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
    }
}
