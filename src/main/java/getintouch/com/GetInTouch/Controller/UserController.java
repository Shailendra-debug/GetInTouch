package getintouch.com.GetInTouch.Controller;

import getintouch.com.GetInTouch.DTO.Users.UserRegisterRequestDto;
import getintouch.com.GetInTouch.DTO.Users.UserResponseDto;
import getintouch.com.GetInTouch.DTO.Users.UserUpdateRequestDto;
import getintouch.com.GetInTouch.Service.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* ---------------- CREATE ---------------- */

    @PostMapping("/add")
    // registration should be public
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserRegisterRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    /* ---------------- READ ---------------- */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    /* ---------------- UPDATE ---------------- */

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDto request) {

        return ResponseEntity.ok(userService.update(id, request));
    }

    /* ---------------- DELETE ---------------- */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------------- ROLE MANAGEMENT ---------------- */

    @PutMapping("/{id}/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> makeAdmin(@PathVariable Long id) {

        userService.makeAdmin(id);
        return ResponseEntity.ok("User promoted to ADMIN");
    }
}
