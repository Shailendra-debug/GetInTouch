package getintouch.com.GetInTouch.Configuration;

import getintouch.com.GetInTouch.Entity.User.Role;
import getintouch.com.GetInTouch.Entity.User.User;
import getintouch.com.GetInTouch.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${BOOTSTRAP_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${BOOTSTRAP_ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {

        // 1️⃣ Only when DB is empty
        if (userRepository.count() > 0) {
            return;
        }

        // 2️⃣ Credentials must exist
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            System.out.println("⚠️ Bootstrap admin skipped (env vars missing)");
            return; // ❗ DO NOT throw exception
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        admin.setAccountLocked(false);

        userRepository.save(admin);

        System.out.println("✅ Initial ADMIN created");
    }
}

