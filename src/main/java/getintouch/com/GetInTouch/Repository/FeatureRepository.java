package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.HomePage.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    List<Feature> findByActiveTrueOrderByDisplayOrderAsc();

    List<Feature> findAllByOrderByDisplayOrderAsc();

    boolean existsByDisplayOrder(Integer displayOrder);

    List<Feature> findByActiveFalseOrderByDisplayOrderAsc();
}