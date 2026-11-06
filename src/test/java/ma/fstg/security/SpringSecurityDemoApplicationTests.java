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

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void accueilAccessibleSansAuthentification() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void pageLoginPersonnaliseeAccessible() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void espaceUtilisateurRedirigeVersLoginSiAnonyme() throws Exception {
        mvc.perform(get("/user/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void connexionUserAvecBonMotDePasse() throws Exception {
        mvc.perform(login("user", "1111")).andExpect(authenticated().withUsername("user"));
    }

    @Test
    void connexionAvecMauvaisMotDePasseRefusee() throws Exception {
        mvc.perform(login("user", "mauvais"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void userAccedeAuDashboardUtilisateur() throws Exception {
        mvc.perform(get("/user/dashboard").with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void userRecoit403SurDashboardAdmin() throws Exception {
        mvc.perform(get("/admin/dashboard").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAccedeAuxDeuxEspaces() throws Exception {
        mvc.perform(login("admin", "1234")).andExpect(authenticated().withRoles("ADMIN", "USER"));
        mvc.perform(get("/user/dashboard").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk());
    }

    @Test
    void deconnexionRedirigeVersLogin() throws Exception {
        mvc.perform(logout())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?logout"));
    }

    private FormLoginRequestBuilder login(String username, String password) {
        return formLogin("/login").user(username).password(password);
    }
}
