package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.HomePage.SliderRequest;
import getintouch.com.GetInTouch.DTO.HomePage.SliderResponse;
import getintouch.com.GetInTouch.Service.HomePage.SliderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Slider APIs", description = "Manage homepage sliders")
@RestController
@RequestMapping("/api/sliders")
@AllArgsConstructor
public class SliderController {

    private final SliderService sliderService;

    /* =====================================================
       GET ACTIVE SLIDERS (PUBLIC)
       ===================================================== */
    @Operation(summary = "Get Active Sliders", description = "Fetch all active sliders (Public)")
    @ApiResponse(responseCode = "200", description = "Sliders fetched successfully")
    @GetMapping("/active")
    public ResponseEntity<List<SliderResponse>> getActiveSliders() {
        return ResponseEntity.ok(sliderService.getActiveSliders());
    }

    /* =====================================================
       GET ALL SLIDERS (ADMIN)
       ===================================================== */
    @Operation(summary = "Get All Sliders", description = "Fetch all sliders (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Sliders fetched successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SliderResponse>> getSliders() {
        return ResponseEntity.ok(sliderService.getSliders());
    }

    /* =====================================================
       CREATE SLIDER (ADMIN)
       ===================================================== */
    @Operation(summary = "Create Slider", description = "Create a new slider (ADMIN only)")
    @ApiResponse(responseCode = "201", description = "Slider created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SliderResponse> createSlider(
            @Valid @RequestBody SliderRequest request) {

        return new ResponseEntity<>(
                sliderService.createSlider(request),
                HttpStatus.CREATED
        );
    }

    /* =====================================================
       UPDATE SLIDER (ADMIN)
       ===================================================== */
    @Operation(summary = "Update Slider", description = "Update slider details (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Slider updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SliderResponse> updateSlider(
            @PathVariable Long id,
            @Valid @RequestBody SliderRequest request) {

        return ResponseEntity.ok(sliderService.updateSlider(id, request));
    }

    /* =====================================================
       DELETE SLIDER (ADMIN)
       ===================================================== */
    @Operation(summary = "Delete Slider", description = "Delete a slider (ADMIN only)")
    @ApiResponse(responseCode = "204", description = "Slider deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSlider(@PathVariable Long id) {

        sliderService.deleteSlider(id);
        return ResponseEntity.noContent().build();
    }
}