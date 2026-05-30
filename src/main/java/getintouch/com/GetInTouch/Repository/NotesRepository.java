package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Note.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface NotesRepository extends JpaRepository<Notes, Long> {


    List<Notes> findByActiveTrue();

    Optional<Notes> findByIdAndActiveTrue(Long id);

    List<Notes> findByTitleContainingIgnoreCase(String keyword);

    List<Notes> findByPriceLessThanEqual(BigDecimal price);

    List<Notes> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT n FROM Notes n WHERE n.active = true ORDER BY n.createdAt DESC")
    List<Notes> findLatestActiveNotes();


}