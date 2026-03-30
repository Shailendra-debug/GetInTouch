package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.CreateOrderRequest;
import getintouch.com.GetInTouch.DTO.Note.OrderResponse;
import getintouch.com.GetInTouch.Service.Note.OrderService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "User order APIs")
public class OrderController {

    private final OrderService service;

    // 🧾 Create Order
    @Operation(summary = "Create order for a note")
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestBody CreateOrderRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(service.createOrder(userId, request));
    }

    // 📤 Upload Screenshot
    @Operation(summary = "Upload payment screenshot")
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> upload(
            @PathVariable Long id,
            @RequestParam String screenshotUrl
    ) {
        service.uploadScreenshot(id, screenshotUrl);
        return ResponseEntity.ok("Uploaded");
    }
}