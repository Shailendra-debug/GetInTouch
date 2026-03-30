package getintouch.com.GetInTouch.Configuration;

import getintouch.com.GetInTouch.Entity.HomePage.Marquee;
import getintouch.com.GetInTouch.Repository.MarqueeRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class MarqueeConfig {
    private final MarqueeRepository marqueeRepository;
    @Bean
    CommandLineRunner initMarquee() {
        return args -> {
            if (marqueeRepository.count() == 0) {
                Marquee marquee = new Marquee();
                marquee.setText("Welcome to GetInTouch!");
                marquee.setActive(true);
                marqueeRepository.save(marquee);
            }
        };
    }

}
