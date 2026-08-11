package getintouch.com.GetInTouch.Service.HomePage;


import getintouch.com.GetInTouch.DTO.HomePage.FeatureCreateRequest;
import getintouch.com.GetInTouch.DTO.HomePage.FeatureResponse;
import getintouch.com.GetInTouch.DTO.HomePage.FeatureUpdateRequest;
import getintouch.com.GetInTouch.Entity.HomePage.Feature;
import getintouch.com.GetInTouch.Repository.FeatureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;


    // ================= CREATE =================

    @Override
    public FeatureResponse createFeature(FeatureCreateRequest request) {

        Feature feature = new Feature();

        feature.setTitle(request.getTitle().trim());
        feature.setDescription(request.getDescription().trim());
        feature.setImageUrl(request.getImageUrl());
        feature.setDisplayOrder(request.getDisplayOrder());
        feature.setActive(true);

        Feature savedFeature = featureRepository.save(feature);

        return mapToResponse(savedFeature);
    }


    // ================= GET BY ID =================

    @Override
    @Transactional()
    public FeatureResponse getFeatureById(Long id) {

        Feature feature = featureRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Feature not found with id: " + id)
                );

        return mapToResponse(feature);
    }


    // ================= GET ALL =================

    @Override
    @Transactional()
    public List<FeatureResponse> getAllFeatures() {

        return featureRepository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ================= GET ACTIVE =================

    @Override
    @Transactional()
    public List<FeatureResponse> getActiveFeatures() {

        return featureRepository
                .findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<FeatureResponse> getInactiveFeatures() {

        return featureRepository
                .findByActiveFalseOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= UPDATE =================

    @Override
    public FeatureResponse updateFeature(Long id, FeatureUpdateRequest request) {
        Feature feature = featureRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Feature not found with id: " + id)
                );

        feature.setTitle(request.getTitle().trim());
        feature.setDescription(request.getDescription().trim());
        feature.setImageUrl(request.getImageUrl());
        feature.setDisplayOrder(request.getDisplayOrder());

        Feature updatedFeature = featureRepository.save(feature);

        return mapToResponse(updatedFeature);
    }





    // ================= ACTIVATE =================

    @Override
    public FeatureResponse activateFeature(Long id) {

        Feature feature = featureRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Feature not found with id: " + id)
                );

        feature.setActive(true);

        Feature updatedFeature = featureRepository.save(feature);

        return mapToResponse(updatedFeature);
    }


    // ================= DEACTIVATE =================

    @Override
    public FeatureResponse deactivateFeature(Long id) {

        Feature feature = featureRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Feature not found with id: " + id)
                );

        feature.setActive(false);

        Feature updatedFeature = featureRepository.save(feature);

        return mapToResponse(updatedFeature);
    }


    // ================= DELETE =================

    @Override
    public void deleteFeature(Long id) {

        Feature feature = featureRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Feature not found with id: " + id)
                );

        featureRepository.delete(feature);
    }


    // ================= MAPPER =================

    private FeatureResponse mapToResponse(Feature feature) {

        FeatureResponse response = new FeatureResponse();

        response.setId(feature.getId());
        response.setTitle(feature.getTitle());
        response.setDescription(feature.getDescription());
        response.setImageUrl(feature.getImageUrl());
        response.setDisplayOrder(feature.getDisplayOrder());
        response.setActive(feature.getActive());
        response.setCreatedAt(feature.getCreatedAt());
        response.setUpdatedAt(feature.getUpdatedAt());

        return response;
    }
}
