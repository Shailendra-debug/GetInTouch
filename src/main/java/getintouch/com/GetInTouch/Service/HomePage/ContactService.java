package getintouch.com.GetInTouch.Service.HomePage;

import getintouch.com.GetInTouch.DTO.HomePage.ContactUsDTO;
import getintouch.com.GetInTouch.DTO.HomePage.Contect_Status;
import getintouch.com.GetInTouch.Entity.HomePage.Contact;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.ContactRepository;
import getintouch.com.GetInTouch.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactService {

    private final ContactRepository repo;
    private final UserRepository userRepository;


    public Contact save(ContactUsDTO contact,Long userId) {
        User user=userRepository.getReferenceById(userId);
        Contact new_contact=new Contact();
        new_contact.setEmail(user.getEmail());
        new_contact.setRead(false);
        new_contact.setStatus(Contect_Status.PENDING);
        new_contact.setName(user.getFullName());
        new_contact.setSubject(contact.getSubject());
        new_contact.setMessage(contact.getMessage());
        new_contact.setUserId(userId);
        return repo.save(new_contact);
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

    public Contact updateStatus(Long id, Contect_Status status) {
        Contact contact = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setStatus(status);
        return repo.save(contact);
    }

}