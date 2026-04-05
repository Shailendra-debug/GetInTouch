package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.User.RegisterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RegisterUserRepository extends JpaRepository<RegisterUser, Long> {

    Optional<RegisterUser> findByEmail(String email);

    Optional<RegisterUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
