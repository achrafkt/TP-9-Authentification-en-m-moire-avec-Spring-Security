package ma.fstg.security.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user_dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin_dashboard";
    }

    // Page de connexion personnalisée (Étape 8)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Page affichée lors d'un refus d'accès (HTTP 403)
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access_denied";
    }
}
