package getintouch.com.GetInTouch.Controller;
import getintouch.com.GetInTouch.Entity.HomePage.Contact;
import getintouch.com.GetInTouch.Service.HomePage.ContactService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Contact API", description = "Contact Us operations")
@RestController
@RequestMapping("/api/contact")
@Validated
@SecurityRequirement(name = "bearerAuth") // 🔐 JWT applied
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    // ✅ USER: Send message
    @Operation(summary = "Send contact message")
    @PostMapping
    public Contact send(@Valid @RequestBody Contact contact) {

        Long userId = SecurityUtil.getCurrentUserId();

        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }

        contact.setUserId(userId);

        return service.save(contact);
    }

    // 🔐 ADMIN: Get all messages
    @Operation(summary = "Get all messages (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<Contact> getAll() {
        return service.getAll();
    }

    // 🔐 ADMIN: Get unread messages
    @Operation(summary = "Get unread messages (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/unread")
    public List<Contact> getUnread() {
        return service.getUnread();
    }

    // 🔐 ADMIN: Mark as read
    @Operation(summary = "Mark message as read")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/read/{id}")
    public Contact markAsRead(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    // 🔐 ADMIN: Update status
    @Operation(summary = "Update message status (PENDING/RESOLVED)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/status/{id}")
    public Contact updateStatus(@PathVariable Long id,
                                @RequestParam String status) {
        return service.updateStatus(id, status);
    }
}
