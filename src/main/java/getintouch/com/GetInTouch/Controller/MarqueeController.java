package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.HomePage.MarqueeRequest;
import getintouch.com.GetInTouch.DTO.HomePage.MarqueeResponse;
import getintouch.com.GetInTouch.Service.HomePage.MarqueeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marquee")
@AllArgsConstructor

public class MarqueeController {

    private final MarqueeService marqueeService;

    // PUBLIC GET
    @GetMapping("/active")
    public ResponseEntity<MarqueeResponse> getActiveMarquee() {
        return ResponseEntity.ok(marqueeService.getActiveMarquee());
    }


    // ADMIN PUT
    @PutMapping("/put")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarqueeResponse> updateMarquee(
            @RequestBody MarqueeRequest request) {
        return ResponseEntity.ok(marqueeService.updateMarquee(1L, request));
    }
}
