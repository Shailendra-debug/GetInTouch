package getintouch.com.GetInTouch.Service.HomePage;


import getintouch.com.GetInTouch.DTO.HomePage.FeatureCreateRequest;
import getintouch.com.GetInTouch.DTO.HomePage.FeatureResponse;
import getintouch.com.GetInTouch.DTO.HomePage.FeatureUpdateRequest;

import java.util.List;

public interface FeatureService {

    FeatureResponse createFeature(FeatureCreateRequest request);

    FeatureResponse getFeatureById(Long id);

    List<FeatureResponse> getAllFeatures();

    List<FeatureResponse> getActiveFeatures();

    List<FeatureResponse> getInactiveFeatures();

    FeatureResponse updateFeature(
            Long id,
            FeatureUpdateRequest request
    );

    FeatureResponse activateFeature(Long id);

    FeatureResponse deactivateFeature(Long id);

    void deleteFeature(Long id);
}