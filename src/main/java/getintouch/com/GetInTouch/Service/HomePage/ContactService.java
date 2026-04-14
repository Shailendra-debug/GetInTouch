package getintouch.com.GetInTouch.Service.HomePage;

import getintouch.com.GetInTouch.Entity.HomePage.Contact;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.ContactRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactService {

    private final ContactRepository repo;
    private final UserRepository userRepository;


    public Contact save(Contact contact) {
        User user=userRepository.getReferenceById(contact.getUserId());
        contact.setEmail(user.getEmail());
        return repo.save(contact);
    }

    public List<Contact> getAll() {
        return repo.findAll();
    }

    public List<Contact> getUnread() {
        return repo.findByIsReadFalse();
    }

    public Contact markAsRead(Long id) {
        Contact contact = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setRead(true);
        return repo.save(contact);
    }

    public Contact updateStatus(Long id, String status) {
        Contact contact = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setStatus(status);
        return repo.save(contact);
    }
}