package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.AdminOrderResponse;
import getintouch.com.GetInTouch.DTO.Note.OrdersIdApproveDto;
import getintouch.com.GetInTouch.Entity.Note.OrderStatus;
import getintouch.com.GetInTouch.Service.Note.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService service;

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAll(
    ) {
        return ResponseEntity.ok(service.getAll());
    }

    // 📋 Get Pending Orders

    @GetMapping("/pending")
    public ResponseEntity<List<AdminOrderResponse>> getPending() {
        return ResponseEntity.ok(service.getByStatus(OrderStatus.PENDING));
    }
    // 📋 Get Pending Orders
    @GetMapping("/approved")
    public ResponseEntity<List<AdminOrderResponse>> getApproved() {
        return ResponseEntity.ok(service.getByStatus(OrderStatus.APPROVED));
    }
    // 📋 Get Pending Orders
    @GetMapping("/rejected")
    public ResponseEntity<List<AdminOrderResponse>> getRejected() {
        return ResponseEntity.ok(service.getByStatus(OrderStatus.REJECTED));
    }

    // ✅ Approve
    @PutMapping("/approved")
    public ResponseEntity<String> approve(@RequestBody OrdersIdApproveDto ordersId) {
        service.approve(ordersId.getOrdersId());
        return ResponseEntity.ok("Approved");
    }

    // ❌ Reject
    @PutMapping("/rejected")
    public ResponseEntity<String> reject(@RequestBody OrdersIdApproveDto ordersId) {
        service.reject(ordersId.getOrdersId());
        return ResponseEntity.ok("Rejected");
    }
}