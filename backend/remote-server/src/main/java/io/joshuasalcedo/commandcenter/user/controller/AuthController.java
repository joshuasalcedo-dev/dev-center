package io.joshuasalcedo.commandcenter.user.controller;

import io.joshuasalcedo.commandcenter.user.api.UserService;
import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final UserService userService;

    AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/status")
    ResponseEntity<Map<String, Object>> authStatus(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        String googleId = principal.getAttribute("sub");
        return userService.findByGoogleId(googleId)
                .map(user -> ResponseEntity.ok(Map.<String, Object>of(
                        "authenticated", true,
                        "id", user.id().value(),
                        "name", user.name(),
                        "email", user.email(),
                        "picture", user.picture(),
                        "role", user.role().name()
                )))
                .orElseGet(() -> ResponseEntity.ok(Map.of("authenticated", false)));
    }

    @GetMapping("/user")
    ResponseEntity<UserDTO> user(@AuthenticationPrincipal OAuth2User principal) {
        String googleId = principal.getAttribute("sub");
        return userService.findByGoogleId(googleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
