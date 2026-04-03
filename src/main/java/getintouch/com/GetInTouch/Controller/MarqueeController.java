package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.HomePage.MarqueeRequest;
import getintouch.com.GetInTouch.DTO.HomePage.MarqueeResponse;
import getintouch.com.GetInTouch.Service.HomePage.MarqueeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Marquee APIs", description = "APIs for managing homepage marquee")
@RestController
@RequestMapping("/api/marquee")
@AllArgsConstructor
public class MarqueeController {

    private final MarqueeService marqueeService;

    /* ================= PUBLIC ================= */

    @Operation(
            summary = "Get Active Marquee",
            description = "Fetch currently active marquee text (Public API)"
    )
    @ApiResponse(responseCode = "200", description = "Active marquee fetched successfully")
    @GetMapping("/active")
    public ResponseEntity<MarqueeResponse> getActiveMarquee() {
        return ResponseEntity.ok(marqueeService.getActiveMarquee());
    }

    /* ================= ADMIN ================= */

    @Operation(
            summary = "Get Admin Marquee",
            description = "Fetch full marquee details for admin"
    )
    @ApiResponse(responseCode = "200", description = "Admin marquee fetched successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarqueeResponse> getAdminMarquee() {
        return ResponseEntity.ok(marqueeService.getAdminMarquee());
    }

    @Operation(
            summary = "Update Marquee",
            description = "Update marquee text (Admin only)"
    )
    @ApiResponse(responseCode = "200", description = "Marquee updated successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PutMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarqueeResponse> updateMarquee(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Marquee update request",
                    required = true
            )
            @RequestBody MarqueeRequest request) {

        return ResponseEntity.ok(marqueeService.updateMarquee(1L, request));
    }
}