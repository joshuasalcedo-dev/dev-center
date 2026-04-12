package io.joshuasalcedo.commandcenter.config.security;

import io.joshuasalcedo.commandcenter.apikey.api.ApiKeyService;
import io.joshuasalcedo.commandcenter.apikey.api.dto.ApiKeyDTO;
import io.joshuasalcedo.commandcenter.user.Role;
import io.joshuasalcedo.commandcenter.user.UserId;
import io.joshuasalcedo.commandcenter.user.api.UserService;
import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String DESKTOP_API_KEY_NAME = "desktop-app";

    private final UserService userService;
    private final ApiKeyService apiKeyService;
    private final String frontendUrl;

    OAuth2LoginSuccessHandler(UserService userService,
                              ApiKeyService apiKeyService,
                              @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl) {
        this.userService = userService;
        this.apiKeyService = apiKeyService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String googleId = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");
        String picture = oauth2User.getAttribute("picture");

        UserDTO user = userService.upsertFromOAuth(googleId, name, email, picture);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.role() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                oauth2User, authentication.getCredentials(), authorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        HttpSession session = request.getSession(false);
        boolean isDesktop = session != null && "desktop".equals(session.getAttribute("auth_flow"));

        if (isDesktop) {
            int port = (int) session.getAttribute("local_server_port");
            session.removeAttribute("auth_flow");
            session.removeAttribute("local_server_port");
            String redirectUrl = handleDesktopLogin(user, port);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } else {
            setDefaultTargetUrl(frontendUrl + "/dashboard");
            setAlwaysUseDefaultTargetUrl(true);
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private String handleDesktopLogin(UserDTO user, int port) {
        UserId userId = user.id();

        // Reuse existing desktop key or create a new one
        ApiKeyDTO apiKey = apiKeyService.findAllActiveByOwner(userId).stream()
                .filter(k -> DESKTOP_API_KEY_NAME.equals(k.name()))
                .findFirst()
                .orElseGet(() -> apiKeyService.create(userId, DESKTOP_API_KEY_NAME));

        return "http://localhost:" + port + "/auth/callback?api_key="
                + URLEncoder.encode(apiKey.id(), StandardCharsets.UTF_8)
                + "&user_name=" + URLEncoder.encode(user.name(), StandardCharsets.UTF_8)
                + "&user_email=" + URLEncoder.encode(user.email(), StandardCharsets.UTF_8);
    }
}
