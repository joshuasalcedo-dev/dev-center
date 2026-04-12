package io.joshuasalcedo.commandcenter.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
class AuthCallbackController {

    private static final Logger log = LoggerFactory.getLogger(AuthCallbackController.class);
    private static final Path CREDENTIALS_FILE = Path.of(
            System.getProperty("user.home"), ".dev-center", "credentials.json");

    private final AuthEventService authEventService;
    private final String frontendUrl;

    AuthCallbackController(AuthEventService authEventService,
                           @Value("${app.frontend-url:https://devscentral.com}") String frontendUrl) {
        this.authEventService = authEventService;
        this.frontendUrl = frontendUrl;
    }

    @GetMapping("/auth/callback")
    RedirectView handleCallback(@RequestParam("api_key") String apiKey,
                                @RequestParam(value = "user_name", required = false) String userName,
                                @RequestParam(value = "user_email", required = false) String userEmail) throws IOException {

        Files.createDirectories(CREDENTIALS_FILE.getParent());
        String json = """
                {
                  "api_key": "%s",
                  "user_name": "%s",
                  "user_email": "%s"
                }
                """.formatted(
                apiKey,
                userName != null ? userName : "",
                userEmail != null ? userEmail : ""
        );
        Files.writeString(CREDENTIALS_FILE, json);
        log.info("Credentials saved to {}", CREDENTIALS_FILE);

        // Push credentials to any listening SSE clients (desktop app)
        authEventService.emitCredentials(json);
        log.info("SSE credential event emitted");

        return new RedirectView(frontendUrl + "/auth/success");
    }
}
