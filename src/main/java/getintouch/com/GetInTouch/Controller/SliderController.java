package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.HomePage.SliderRequest;
import getintouch.com.GetInTouch.DTO.HomePage.SliderResponse;
import getintouch.com.GetInTouch.Service.HomePage.SliderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sliders")
@AllArgsConstructor
public class SliderController {

    private final SliderService sliderService;

    // PUBLIC GET
    @GetMapping("/active")
    public ResponseEntity<List<SliderResponse>> getActiveSliders() {
        return ResponseEntity.ok(sliderService.getActiveSliders());
    }

    @GetMapping("/getall")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SliderResponse>> getSliders() {
        return ResponseEntity.ok(sliderService.getSliders());
    }

    // ADMIN POST
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SliderResponse> createSlider(@RequestBody SliderRequest request) {
        return ResponseEntity.ok(sliderService.createSlider(request));
    }

    // ADMIN PUT (Update Slider)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SliderResponse> updateSlider(
            @PathVariable Long id,
            @RequestBody SliderRequest request) {
        return ResponseEntity.ok(sliderService.updateSlider(id, request));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSlider(@PathVariable Long id) {
        sliderService.deleteSlider(id);
        return ResponseEntity.noContent().build();
    }


}
