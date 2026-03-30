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

@Tag(name = "Marquee APIs", description = "Manage homepage marquee text")
@RestController
@RequestMapping("/api/marquee")
@AllArgsConstructor
public class MarqueeController {

    private final MarqueeService marqueeService;

    /* =====================================================
       GET ACTIVE MARQUEE (PUBLIC)
       ===================================================== */
   //b @CrossOrigin(origins = "*")
    @Operation(
            summary = "Get Active Marquee",
            description = "Fetch the currently active marquee text (Public API)"
    )
    @ApiResponse(responseCode = "200", description = "Marquee fetched successfully")
    @GetMapping("/active")
    public ResponseEntity<MarqueeResponse> getActiveMarquee() {
        return ResponseEntity.ok(marqueeService.getActiveMarquee());
    }

    /* =====================================================
       UPDATE MARQUEE (ADMIN)
       ===================================================== */
    @Operation(
            summary = "Update Marquee",
            description = "Update homepage marquee text (ADMIN only)"
    )
    @ApiResponse(responseCode = "200", description = "Marquee updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<MarqueeResponse> updateMarquee(
            @RequestBody MarqueeRequest request) {
        return ResponseEntity.ok(marqueeService.updateMarquee(1L, request));
    }
}