package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Service.Course.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Course APIs", description = "Manage courses (Admin & Users)")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /* =====================================================
       CREATE COURSE (ADMIN)
       ===================================================== */
    @Operation(summary = "Create Course", description = "Create a new course (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Course created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
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
    @Operation(summary = "Get All Courses", description = "Fetch all available courses (USER & ADMIN)")
    @ApiResponse(responseCode = "200", description = "Courses fetched successfully")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /* =====================================================
       GET COURSE BY ID
       ===================================================== */
    @Operation(summary = "Get Course By ID", description = "Fetch a specific course by ID")
    @ApiResponse(responseCode = "200", description = "Course found")
    @ApiResponse(responseCode = "404", description = "Course not found")
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
    @Operation(summary = "Update Course", description = "Update course details (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Course updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
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
    @Operation(summary = "Delete Course", description = "Delete a course (ADMIN only)")
    @ApiResponse(responseCode = "204", description = "Course deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
    }
}