package io.joshuasalcedo.commandcenter.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
class CredentialsController {

    private static final Path CREDENTIALS_FILE = Path.of(
            System.getProperty("user.home"), ".dev-center", "credentials.json");

    @GetMapping("/auth/credentials")
    ResponseEntity<String> getCredentials() throws IOException {
        if (!Files.exists(CREDENTIALS_FILE)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(Files.readString(CREDENTIALS_FILE));
    }
}
