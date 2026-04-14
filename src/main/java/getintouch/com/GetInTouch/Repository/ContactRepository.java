package getintouch.com.GetInTouch.Repository;

import getintouch.com.GetInTouch.Entity.HomePage.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByUserId(Long userId);

    List<Contact> findByIsReadFalse(); // unread messages

    List<Contact> findByStatus(String status);
}