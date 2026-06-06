package getintouch.com.GetInTouch.Controller.Public;

import getintouch.com.GetInTouch.DTO.Course.CourseResponseDTO;
import getintouch.com.GetInTouch.Service.Course.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/courses")
@AllArgsConstructor

public class PublicCoursesController {

    private final CourseService courseService;


    @Operation(
            summary = "Get All Active Courses",
            description = "Returns all active courses ordered by course number."
    )
    @GetMapping
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

}
