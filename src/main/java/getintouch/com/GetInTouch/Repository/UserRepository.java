package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.purchasedNotes WHERE u.id = :id")
    Optional<User> findByIdWithNotes(@Param("id") Long id);

    // Fetch all users and their notes in 1 query
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.purchasedNotes")
    List<User> findAllWithNotes();

}
