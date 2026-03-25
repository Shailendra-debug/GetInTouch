package getintouch.com.GetInTouch.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/user")
    public String user() {
        return "USER access";
    }

    @GetMapping("/admin")
    public String admin() {
        return "ADMIN access";
    }
}
