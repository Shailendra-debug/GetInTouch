package getintouch.com.GetInTouch.Controller;


import getintouch.com.GetInTouch.DTO.Admin.AdminPanelDtu;
import getintouch.com.GetInTouch.Service.Admin.AdminPanelService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminPanelController {

    private final AdminPanelService adminPanelService;

    @GetMapping("/dashboard")
    public AdminPanelDtu dashboard() {
        return AdminPanelDtu.builder()
                .totalPendingNotesOrder(adminPanelService.PendingOrder())
                .totalQuiz(adminPanelService.getAllQuiz())
                .totalQuizAttempt(adminPanelService.getAllAttemptQuiz())
                .totalQuizSubmit(adminPanelService.getAllSubmittedQuiz())
                .totalST(adminPanelService.getAllUser())
                .build();
    }
}
