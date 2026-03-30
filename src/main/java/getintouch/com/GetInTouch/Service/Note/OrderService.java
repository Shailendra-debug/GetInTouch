package getintouch.com.GetInTouch.Service.Note;

import getintouch.com.GetInTouch.DTO.Note.AdminOrderResponse;
import getintouch.com.GetInTouch.DTO.Note.CreateOrderRequest;
import getintouch.com.GetInTouch.DTO.Note.OrderResponse;
import getintouch.com.GetInTouch.Entity.Note.Notes;
import getintouch.com.GetInTouch.Entity.Note.Order;
import getintouch.com.GetInTouch.Entity.Note.OrderStatus;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.NotesRepository;
import getintouch.com.GetInTouch.Repository.OrderRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import getintouch.com.GetInTouch.Service.Auth.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotesRepository notesRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    // 🧾 Create Order
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

        Notes note = notesRepository.findById(request.getNoteId())
                .orElseThrow(() -> new RuntimeException("Note not found"));

        Order order = Order.builder()
                .userId(userId)
                .note(note) // ✅ FIXED (use relation)
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        return OrderResponse.builder()
                .orderId(order.getId())
                .noteId(note.getId())
                .status(order.getStatus().name())
                .paymentQrUrl(note.getPaymentQrUrl())
                .build();
    }

    // 📤 Upload Screenshot
    public void uploadScreenshot(Long orderId, String url) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentScreenshotUrl(url);
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);
    }

    // 👨‍💼 Admin - Get Orders By Status
    public List<AdminOrderResponse> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(o -> AdminOrderResponse.builder()
                        .orderId(o.getId())
                        .userId(o.getUserId())
                        .noteId(o.getNote().getId()) // ✅ FIXED
                        .screenshotUrl(o.getPaymentScreenshotUrl())
                        .status(o.getStatus().name())
                        .createdAt(o.getCreatedAt().toString())
                        .build())
                .toList();
    }

    // 👨‍💼 Admin - Get All Orders
    public List<AdminOrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(o -> AdminOrderResponse.builder()
                        .orderId(o.getId())
                        .userId(o.getUserId())
                        .noteId(o.getNote().getId()) // ✅ FIXED
                        .screenshotUrl(o.getPaymentScreenshotUrl())
                        .status(o.getStatus().name())
                        .createdAt(o.getCreatedAt().toString())
                        .build())
                .toList();
    }

    // ✅ Approve Order
    public void approve(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.APPROVED);
        orderRepository.save(order);

        User user = userRepository.getReferenceById(order.getUserId());

        // 📧 Send Email
        emailService.sendMail(
                user.getEmail(),
                "Payment Approved 🎉",
                "Your payment is approved. You can now download your notes."
        );
    }

    // ❌ Reject Order
    public void reject(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.REJECTED);
        orderRepository.save(order);

        User user = userRepository.getReferenceById(order.getUserId());

        // 📧 Send Email
        emailService.sendMail(
                user.getEmail(),
                "Payment Rejected ❌",
                "Your payment was not approved. Please try again."
        );
    }



    // ✅ Get Approved Notes (MAIN FEATURE 🔥)
    public List<OrderResponse> getApprovedNotes(Long userId) {
        return orderRepository.findAllApprovedNotes(userId)
                .stream()
                .map(note -> OrderResponse.builder()
                        .noteId(note.getId())
                        .paymentQrUrl(note.getPaymentQrUrl())
                        .status("APPROVED")
                        .build())
                .toList();
    }

    public Notes getApprovedNote(Long userId, Long orderId) {
        return orderRepository.findApprovedNote(userId, orderId)
                .orElseThrow(() -> new RuntimeException("Note not accessible"));
    }
}