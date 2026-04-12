package getintouch.com.GetInTouch.Service.HomePage;

import getintouch.com.GetInTouch.DTO.HomePage.MarqueeRequest;
import getintouch.com.GetInTouch.DTO.HomePage.MarqueeResponse;
import getintouch.com.GetInTouch.Entity.HomePage.Marquee;
import getintouch.com.GetInTouch.Mapper.MarqueeMapper;
import getintouch.com.GetInTouch.Repository.MarqueeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class MarqueeService {


    private final MarqueeRepository marqueeRepository;

    private final MarqueeMapper marqueeMapper;

    public MarqueeResponse getActiveMarquee() {
        return marqueeRepository.findFirstByActiveTrue()
                .map(marqueeMapper::toResponse)
                .orElse(new MarqueeResponse("", false,null));
    }

    public MarqueeResponse updateMarquee(Long id, MarqueeRequest request) {
        Marquee marquee = marqueeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marquee not found"));

        marquee.setText(request.getText());
        marquee.setActive(request.isActive());
        marquee.setUrl(request.getUrl());

        return marqueeMapper.toResponse(marqueeRepository.save(marquee));
    }

    public MarqueeResponse getAdminMarquee(){

        return marqueeRepository.findById(1L)
                .map(marqueeMapper::toResponse)
                .orElse(new MarqueeResponse("", false,null));

    }

    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<Character>>list=new ArrayList<>();
        List<List<String>>ans=new ArrayList<>();
        Map<String,Integer>map=new HashMap<>();
        for (String i:strs){
            List<Character>temp=new ArrayList<>();
            for (char j:i.toCharArray()){
                temp.add(j);
            }
            Collections.sort(temp);
            list.add(temp);
        }
        int index=0;
        for (int i = 0; i < list.size(); i++) {
            String str=list.get(i).toString();
            if (map.containsKey(str)){
                ans.get(map.get(str)).add(strs[i]);
            }else {
                map.put(str, index);
                List<String> temp = new ArrayList<>();
                temp.add(strs[i]);
                ans.add(temp);
                index++;
            }
        }
        return ans;
    }
}

