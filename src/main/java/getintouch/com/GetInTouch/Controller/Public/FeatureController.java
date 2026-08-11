package getintouch.com.GetInTouch.Controller.Public;

import getintouch.com.GetInTouch.DTO.HomePage.FeatureCreateRequest;
import getintouch.com.GetInTouch.DTO.HomePage.FeatureResponse;
import getintouch.com.GetInTouch.DTO.HomePage.FeatureUpdateRequest;
import getintouch.com.GetInTouch.Service.HomePage.FeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/features")
@RequiredArgsConstructor
@Tag(name = "Feature", description = "Manage features")
public class FeatureController {

    private final FeatureService featureService;

    @Operation(summary = "Create feature")
    @PostMapping
    public ResponseEntity<FeatureResponse> createFeature(
            @Valid @RequestBody FeatureCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(featureService.createFeature(request));
    }

    @Operation(summary = "Get feature by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FeatureResponse> getFeatureById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                featureService.getFeatureById(id)
        );
    }

    @Operation(summary = "Get all features")
    @GetMapping
    public ResponseEntity<List<FeatureResponse>> getAllFeatures() {
        return ResponseEntity.ok(
                featureService.getAllFeatures()
        );
    }

    @Operation(summary = "Get active features")
    @GetMapping("/active")
    public ResponseEntity<List<FeatureResponse>> getActiveFeatures() {
        return ResponseEntity.ok(
                featureService.getActiveFeatures()
        );
    }

    @Operation(summary = "Get inactive features")
    @GetMapping("/inactive")
    public ResponseEntity<List<FeatureResponse>> getInactiveFeatures() {
        return ResponseEntity.ok(
                featureService.getInactiveFeatures()
        );
    }

    @Operation(summary = "Update feature")
    @PutMapping("/{id}")
    public ResponseEntity<FeatureResponse> updateFeature(
            @PathVariable Long id,
            @Valid @RequestBody FeatureUpdateRequest request
    ) {
        return ResponseEntity.ok(
                featureService.updateFeature(id, request)
        );
    }

    @Operation(summary = "Activate feature")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<FeatureResponse> activateFeature(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                featureService.activateFeature(id)
        );
    }

    @Operation(summary = "Deactivate feature")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<FeatureResponse> deactivateFeature(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                featureService.deactivateFeature(id)
        );
    }

    @Operation(summary = "Delete feature")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeature(
            @PathVariable Long id
    ) {
        featureService.deleteFeature(id);
        return ResponseEntity.noContent().build();
    }
}
