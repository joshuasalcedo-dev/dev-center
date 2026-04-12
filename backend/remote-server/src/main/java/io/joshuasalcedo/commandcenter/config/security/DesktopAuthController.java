package io.joshuasalcedo.commandcenter.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/public/auth")
class DesktopAuthController {

    @GetMapping("/desktop")
    RedirectView desktopLogin(HttpServletRequest request,
                              @RequestParam("port") int port) {
        request.getSession().setAttribute("auth_flow", "desktop");
        request.getSession().setAttribute("local_server_port", port);
        return new RedirectView("/oauth2/authorization/google");
    }
}
