package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.User.RefreshToken;
import getintouch.com.GetInTouch.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    void deleteByUser(User user);

    boolean existsByToken(String refreshToken);
}
