package getintouch.com.GetInTouch.Mapper;



import getintouch.com.GetInTouch.DTO.HomePage.MarqueeRequest;
import getintouch.com.GetInTouch.DTO.HomePage.MarqueeResponse;
import getintouch.com.GetInTouch.Entity.HomePage.Marquee;
import org.springframework.stereotype.Component;

@Component
public class MarqueeMapper {

    public MarqueeResponse toResponse(Marquee marquee) {
        MarqueeResponse response = new MarqueeResponse();
        response.setText(marquee.getText());
        response.setActive(marquee.getActive());
        response.setUrl(marquee.getUrl());
        return response;
    }

    public Marquee toEntity(MarqueeRequest request) {
        Marquee marquee = new Marquee();
        marquee.setText(request.getText());
        marquee.setActive(request.isActive());
        marquee.setUrl(request.getUrl());
        return marquee;
    }
}