package getintouch.com.GetInTouch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)

public class GetInTouchApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetInTouchApplication.class, args);
	}

}
