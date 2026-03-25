package getintouch.com.GetInTouch.Repository;



import getintouch.com.GetInTouch.Entity.HomePage.Slider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Long> {

    List<Slider> findAllByActiveTrueOrderByDisplayOrderAsc();
}
