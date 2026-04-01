package getintouch.com.GetInTouch.Service.HomePage;

import getintouch.com.GetInTouch.DTO.HomePage.SliderRequest;
import getintouch.com.GetInTouch.DTO.HomePage.SliderResponse;
import getintouch.com.GetInTouch.Entity.HomePage.Slider;
import getintouch.com.GetInTouch.Mapper.SliderMapper;
import getintouch.com.GetInTouch.Repository.SliderRepository;
import getintouch.com.GetInTouch.Service.File.FileUploadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SliderService {


    private final SliderRepository sliderRepository;


    private final SliderMapper sliderMapper;

    private final FileUploadService fileUploadService;


    public List<SliderResponse> getActiveSliders() {
        return sliderRepository.findAllByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(sliderMapper::toResponse)
                .collect(Collectors.toList());
    }
    public List<SliderResponse> getSliders() {
        return sliderRepository.findAll()
                .stream()
                .map(sliderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public SliderResponse createSlider(@RequestParam MultipartFile file) {
        String url= fileUploadService.uploadFile(file,"slider");
            Slider slider = new Slider();
            slider.setImageUrl(url);
            slider.setActive(true);
            slider.setDisplayOrder(1);
        return sliderMapper.toResponse(sliderRepository.save(slider));
    }

    public SliderResponse updateSlider(Long id, SliderRequest request) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slider not found"));

        // map fields from request to existing entity
        slider.setImageUrl(slider.getImageUrl());
        slider.setActive(request.isActive());
        slider.setDisplayOrder(request.getDisplayOrder());

        return sliderMapper.toResponse(sliderRepository.save(slider));
    }

    public void deleteSlider(Long id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slider not found"));
        sliderRepository.delete(slider);
    }

}

