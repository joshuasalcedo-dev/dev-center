package io.joshuasalcedo.commandcenter.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
class AuthCallbackController {

    private static final Logger log = LoggerFactory.getLogger(AuthCallbackController.class);
    private static final Path CREDENTIALS_FILE = Path.of(
            System.getProperty("user.home"), ".dev-center", "credentials.json");

    private final AuthEventService authEventService;

    AuthCallbackController(AuthEventService authEventService) {
        this.authEventService = authEventService;
    }

    @GetMapping(value = "/auth/callback", produces = MediaType.TEXT_HTML_VALUE)
    String handleCallback(@RequestParam("api_key") String apiKey,
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

        return """
                <!DOCTYPE html>
                <html>
                <head><title>Dev Center</title></head>
                <body style="font-family: system-ui; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background: #0a0a0a; color: #fafafa;">
                  <div style="text-align: center;">
                    <h1>Signed in successfully</h1>
                    <p>You can close this tab and return to the desktop app.</p>
                  </div>
                </body>
                </html>
                """;
    }
}
