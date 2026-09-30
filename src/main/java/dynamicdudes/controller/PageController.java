package dynamicdudes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login.html";
    }

    @GetMapping("/")
    public String home() {
        return "forward:/home.html";
    }

    @GetMapping("/dashboard")
    public String customerDashboard() {
        return "forward:/dashboard.html";
    }

    @GetMapping("/admin")
    public String adminDashboard() {
        return "forward:/admin.html";
    }
}
