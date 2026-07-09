package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Users.RegisterSendOtpResponseDto;
import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.DTO.Users.UserUpdateRequestDto;
import getintouch.com.GetInTouch.Service.File.FileUploadService;
import getintouch.com.GetInTouch.Service.User.UserService;
import getintouch.com.GetInTouch.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Tag(name = "User APIs", description = "Manage users and roles")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final FileUploadService uploadService;

    /* =====================================================
       REGISTER USER (PUBLIC)
       ===================================================== */
    @Operation(summary = "Register User", description = "Register a new user (Public API)")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping
    public ResponseEntity<RegisterSendOtpResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    @Operation(summary = "Register User", description = "Register a new user By Admin")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/registerByAdmin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserResponseDto> registerByAdmin(
            @Valid @RequestBody UserRegisterRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerByAdmin(request));
    }

    /* =====================================================
       GET USER BY ID / GMAIL
       ===================================================== */
    @Operation(summary = "Get User By ID", description = "Fetch user details by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Get User By Gmail", description = "Fetch user details by Gmail")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    // CHANGED: Added "/email" to separate this endpoint from the dynamic id path above
    @GetMapping("/email/{gmail}")
    @PreAuthorize("hasRole('ADMIN')")
    // GOOD PRACTICE: Match your @PathVariable parameter case exactly with the URI variable name
    public ResponseEntity<UserResponseDto> getByGmail(@PathVariable("gmail") String gmail) {
        return ResponseEntity.ok(userService.getByGmail(gmail));
    }
    /* =====================================================
       GET ALL USERS (ADMIN)
       ===================================================== */


    @Operation(summary = "Get All Users", description = "Fetch all users (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "Users fetched successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    /* =====================================================
       UPDATE USER
       ===================================================== */
    @Operation(summary = "Update User", description = "Update user profile (Self or ADMIN)")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto request) {

        return ResponseEntity.ok(userService.update(id, request));
    }

    /* =====================================================
       DELETE USER (ADMIN)
       ===================================================== */
    @Operation(summary = "Delete User", description = "Delete user (ADMIN only)")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* =====================================================
       PROMOTE USER TO ADMIN
       ===================================================== */
    @Operation(summary = "Promote to Admin", description = "Promote a user to ADMIN role")
    @ApiResponse(responseCode = "200", description = "User promoted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PutMapping("/{id}/role/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> makeAdmin(@PathVariable Long id) {

        userService.makeAdmin(id);
        return ResponseEntity.ok("User promoted to ADMIN");
    }

    @Operation(summary = "Promote to User", description = "Promote a user to ADMIN role")
    @ApiResponse(responseCode = "200", description = "User promoted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
    @PutMapping("/{id}/role/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeAdmin(@PathVariable Long id) {

        userService.removeAdmin(id);
        return ResponseEntity.ok("ADMIN promoted to User");
    }
    @Operation(
            summary = "Get All Inactive Users",
            description = "Returns a paginated list of all inactive users."
    )
    @ApiResponse(responseCode = "200", description = "Inactive users retrieved successfully")
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllInactiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(
                userService.getAllInactiveUsers(page, size, sortBy, direction)
        );
    }

    @Operation(
            summary = "Deactivate User",
            description = "Deactivates an active user account (ADMIN only)"
    )
    @ApiResponse(responseCode = "200", description = "User deactivated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get All Active Users",
            description = "Returns a paginated list of all active users."
    )
    @ApiResponse(responseCode = "200", description = "Active users retrieved successfully")
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllActiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(
                userService.getAllActiveUsers(page, size, sortBy, direction)
        );
    }

    @Operation(
            summary = "Activate User",
            description = "Reactivates a deactivated user account (ADMIN only)"
    )
    @ApiResponse(responseCode = "200", description = "User activated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Upload Profile Photo",
            description = "Upload profile photo for the currently logged-in user"
    )
    @PostMapping("/profile/photo")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Please select a file");
        }

        Long currentUserId = SecurityUtil.getCurrentUserId();

        String imageUrl = uploadService.uploadFile(file, "profile");

        return ResponseEntity.ok(
                userService.UpdateProfileImageUrl(imageUrl, currentUserId)
        );
    }
}
