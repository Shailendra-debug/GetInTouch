package getintouch.com.GetInTouch.Service.HomePage;

import getintouch.com.GetInTouch.DTO.HomePage.MarqueeRequest;
import getintouch.com.GetInTouch.DTO.HomePage.MarqueeResponse;
import getintouch.com.GetInTouch.Entity.HomePage.Marquee;
import getintouch.com.GetInTouch.Mapper.MarqueeMapper;
import getintouch.com.GetInTouch.Repository.MarqueeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MarqueeService {


    private final MarqueeRepository marqueeRepository;

    private final MarqueeMapper marqueeMapper;

    public MarqueeResponse getActiveMarquee() {
        return marqueeRepository.findFirstByActiveTrue()
                .map(marqueeMapper::toResponse)
                .orElse(new MarqueeResponse("", false));
    }

    public MarqueeResponse updateMarquee(Long id, MarqueeRequest request) {
        Marquee marquee = marqueeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marquee not found"));

        marquee.setText(request.getText());
        marquee.setActive(request.isActive());

        return marqueeMapper.toResponse(marqueeRepository.save(marquee));
    }
}

