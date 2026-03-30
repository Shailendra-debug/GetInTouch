package getintouch.com.GetInTouch.Controller.Note;

import getintouch.com.GetInTouch.DTO.Note.AdminOrderResponse;
import getintouch.com.GetInTouch.DTO.Note.OrdersIdApproveDto;
import getintouch.com.GetInTouch.Entity.Note.OrderStatus;
import getintouch.com.GetInTouch.Service.Note.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Orders", description = "Manage orders (approve/reject/view)")
public class AdminOrderController {

    private final OrderService service;

    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get pending orders")
    @GetMapping("/pending")
    public ResponseEntity<List<AdminOrderResponse>> getPending() {
        return ResponseEntity.ok(service.getByStatus(OrderStatus.PENDING));
    }

    @Operation(summary = "Get approved orders")
    @GetMapping("/approved")
    public ResponseEntity<List<AdminOrderResponse>> getApproved() {
        return ResponseEntity.ok(service.getByStatus(OrderStatus.APPROVED));
    }

    @Operation(summary = "Get rejected orders")
    @GetMapping("/rejected")
    public ResponseEntity<List<AdminOrderResponse>> getRejected() {
        return ResponseEntity.ok(service.getByStatus(OrderStatus.REJECTED));
    }

    @Operation(summary = "Approve order")
    @PutMapping("/approved")
    public ResponseEntity<String> approve(@RequestBody OrdersIdApproveDto ordersId) {
        service.approve(ordersId.getOrdersId());
        return ResponseEntity.ok("Approved");
    }

    @Operation(summary = "Reject order")
    @PutMapping("/rejected")
    public ResponseEntity<String> reject(@RequestBody OrdersIdApproveDto ordersId) {
        service.reject(ordersId.getOrdersId());
        return ResponseEntity.ok("Rejected");
    }
}