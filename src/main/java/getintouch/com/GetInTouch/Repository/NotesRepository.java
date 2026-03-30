package getintouch.com.GetInTouch.Repository;


import getintouch.com.GetInTouch.Entity.Note.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotesRepository extends JpaRepository<Notes, Long> {
}