package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Course.CourseRequestDTO;
import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Service.Course.CourseService;
import getintouch.com.GetInTouch.Service.File.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(
        name = "Course Management",
        description = "APIs for managing courses. Admin can create, update, activate and delete courses. Users can view and search active courses."
)
public class CourseController {

    private final CourseService courseService;

    private final FileUploadService uploadService;

    // ================= ADMIN =================

    @Operation(
            summary = "Create Course",
            description = "Creates a new course. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Course already exists")
    })
   // @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(
            @Valid @RequestBody CourseRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(requestDTO));
    }

    @Operation(
            summary = "Update Course",
            description = "Updates an existing course by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
   // @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, requestDTO));
    }

    @Operation(
            summary = "Delete Course",
            description = "Soft deletes a course by marking it inactive."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    //@SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable Long id
    ) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Activate Course",
            description = "Activates a previously inactive course."
    )
    //@SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<CourseResponseDTO> activateCourse(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(courseService.activateCourse(id));
    }

    @Operation(
            summary = "Deactivate Course",
            description = "Deactivates an active course."
    )
//@SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CourseResponseDTO> deactivateCourse(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(courseService.deactivateCourse(id));
    }

    @Operation(
            summary = "Get All Courses For Admin",
            description = "Returns all courses including inactive ones."
    )
    //@SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // ================= USER =================

    @Operation(
            summary = "Get All Active Courses",
            description = "Returns all active courses ordered by course number."
    )
    @GetMapping("/active")
    public ResponseEntity<List<CourseResponseDTO>> getAllActiveCourses() {
        return ResponseEntity.ok(courseService.getAllActiveCourses());
    }

    @Operation(
            summary = "Get Course By ID",
            description = "Returns an active course by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(
            @Parameter(description = "Course ID", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(
            summary = "Get Course By Course Number",
            description = "Returns a course using its course number."
    )
    @GetMapping("/number/{courseNumber}")
    public ResponseEntity<CourseResponseDTO> getCourseByCourseNumber(
            @Parameter(description = "Course Number", example = "101")
            @PathVariable Long courseNumber
    ) {
        return ResponseEntity.ok(
                courseService.getCourseByCourseNumber(courseNumber)
        );
    }

    @Operation(
            summary = "Search Courses",
            description = "Search active courses by keyword."
    )
    @GetMapping("/search")
    public ResponseEntity<List<CourseResponseDTO>> searchCourses(
            @Parameter(description = "Search keyword", example = "Java")
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                courseService.searchCourses(keyword)
        );
    }

    @Operation(summary = "Create Thumbnail", description = "Create a new Thumbnail For Course (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Thumbnail created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PostMapping("thumbnail")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSlider(
            @RequestParam MultipartFile file) {
        String url=uploadService.uploadFile(file,"course");
        return ResponseEntity.ok(url);
    }
}