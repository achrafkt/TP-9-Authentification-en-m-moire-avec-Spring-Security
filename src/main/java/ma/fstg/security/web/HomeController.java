package ma.fstg.security.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 首页：所有人都可以访问（无需登录）
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 用户空间：需要 USER 角色
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user_dashboard";
    }

    // 管理员空间：需要 ADMIN 角色
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin_dashboard";
    }

    // 自定义登录页面（第 8 步）
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 访问被拒绝时显示的页面（HTTP 403）
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access_denied";
    }
}
