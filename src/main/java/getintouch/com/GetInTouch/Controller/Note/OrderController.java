package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.CreateOrderRequest;
import getintouch.com.GetInTouch.DTO.Note.OrderResponse;
import getintouch.com.GetInTouch.Service.Note.OrderService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    // 🧾 Create Order
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestBody CreateOrderRequest request
    ) {
        Long userId= SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.createOrder(userId, request));
    }

    // 📤 Upload Screenshot
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> upload(
            @RequestParam String screenshotUrl
    ) {
        Long userId= SecurityUtil.getCurrentUserId();
        service.uploadScreenshot(userId, screenshotUrl);
        return ResponseEntity.ok("Uploaded");
    }
}