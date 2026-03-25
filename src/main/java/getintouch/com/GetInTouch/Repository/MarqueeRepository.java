package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.HomePage.Marquee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarqueeRepository extends JpaRepository<Marquee, Long> {

    Optional<Marquee> findFirstByActiveTrue();
}
