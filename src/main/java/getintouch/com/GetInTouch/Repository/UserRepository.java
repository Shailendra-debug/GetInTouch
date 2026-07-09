package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN FETCH u.purchases p
            LEFT JOIN FETCH p.note
            WHERE u.id = :id
            """)
    Optional<User> findByIdWithNotes(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN FETCH u.purchases p
            LEFT JOIN FETCH p.note
            """)
    List<User> findAllWithNotes();

    List<User> findByUsernameContainingIgnoreCase(String username);

    List<User> findByFullNameContainingIgnoreCase(String fullName);

    List<User> findByEmailContainingIgnoreCase(String email);

    Page<User> findByEnabledTrue(Pageable pageable);

    Page<User> findByEnabledFalse(Pageable pageable);


    List<User> findAllByOrderByCreatedAtDesc();

    List<User> findAllByOrderByUsernameAsc();


    Optional<User> findByEmailOrUsername(String email, String username);

    List<User> findByRole(Role role);

    List<User> findByEnabled(Boolean enabled);

    List<User> findByAccountLocked(Boolean accountLocked);

    // =========================
    // DASHBOARD
    // =========================

    @Query("SELECT COUNT(u) FROM User u")
    long totalUsers();

    @Query("""
            SELECT COUNT(u)
            FROM User u
            WHERE u.enabled = true
            """)
    long totalActiveUsers();

    @Query("""
            SELECT COUNT(u)
            FROM User u
            WHERE u.accountLocked = true
            """)
    long totalLockedUsers();

}