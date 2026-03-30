package getintouch.com.GetInTouch.Mapper;

import getintouch.com.GetInTouch.DTO.HomePage.SliderRequest;
import getintouch.com.GetInTouch.DTO.HomePage.SliderResponse;
import getintouch.com.GetInTouch.Entity.HomePage.Slider;
import org.springframework.stereotype.Component;

@Component
public class SliderMapper {

    public SliderResponse toResponse(Slider slider) {
        SliderResponse response = new SliderResponse();
        response.setImageUrl(slider.getImageUrl());
        response.setActive(slider.getActive());
        response.setId(slider.getId());
        return response;
    }

    public Slider toEntity(SliderRequest request) {
        Slider slider = new Slider();
        slider.setImageUrl(request.getImageUrl());
        slider.setActive(request.isActive());
        slider.setDisplayOrder(request.getDisplayOrder());
        return slider;
    }
}
