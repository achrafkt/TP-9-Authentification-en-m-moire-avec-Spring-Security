package ma.fstg.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Étape 9 : validation fonctionnelle des scénarios d'authentification,
 * de contrôle d'accès et de déconnexion.
 */
@SpringBootTest
class SpringSecurityDemoApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    // 初始化 MockMvc 并启用 Spring Security 过滤器
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // 首页无需登录即可访问（200）
    @Test
    void accueilAccessibleSansAuthentification() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    // 自定义登录页面可以公开访问（200）
    @Test
    void pageLoginPersonnaliseeAccessible() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    // 未登录访问用户空间时重定向到 /login
    @Test
    void espaceUtilisateurRedirigeVersLoginSiAnonyme() throws Exception {
        mvc.perform(get("/user/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // 使用 user / 1111 登录成功
    @Test
    void connexionUserAvecBonMotDePasse() throws Exception {
        mvc.perform(login("user", "1111")).andExpect(authenticated().withUsername("user"));
    }

    // 密码错误时登录失败并重定向到 /login?error
    @Test
    void connexionAvecMauvaisMotDePasseRefusee() throws Exception {
        mvc.perform(login("user", "mauvais"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    // USER 角色可以访问用户空间
    @Test
    void userAccedeAuDashboardUtilisateur() throws Exception {
        mvc.perform(get("/user/dashboard").with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }

    // USER 角色访问管理员空间时返回 403
    @Test
    void userRecoit403SurDashboardAdmin() throws Exception {
        mvc.perform(get("/admin/dashboard").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // admin 可以登录并访问用户空间和管理员空间
    @Test
    void adminAccedeAuxDeuxEspaces() throws Exception {
        mvc.perform(login("admin", "1234")).andExpect(authenticated().withRoles("ADMIN", "USER"));
        mvc.perform(get("/user/dashboard").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }

    // 注销后重定向到 /login?logout
    @Test
    void deconnexionRedirigeVersLogin() throws Exception {
        mvc.perform(logout())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?logout"));
    }

    // 辅助方法：构造表单登录请求
    private FormLoginRequestBuilder login(String username, String password) {
        return formLogin("/login").user(username).password(password);
    }
}
