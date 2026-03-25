package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.DTO.Users.UserUpdateRequestDto;
import getintouch.com.GetInTouch.Service.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User APIs", description = "Manage users and roles")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* =====================================================
       REGISTER USER (PUBLIC)
       ===================================================== */
    @Operation(summary = "Register User", description = "Register a new user (Public API)")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    /* =====================================================
       GET USER BY ID
       ===================================================== */
    @Operation(summary = "Get User By ID", description = "Fetch user details by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
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
}