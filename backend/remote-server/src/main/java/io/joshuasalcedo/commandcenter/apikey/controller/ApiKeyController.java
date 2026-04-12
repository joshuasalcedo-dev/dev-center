package io.joshuasalcedo.commandcenter.apikey.controller;

import io.joshuasalcedo.commandcenter.apikey.api.ApiKeyService;
import io.joshuasalcedo.commandcenter.apikey.api.dto.ApiKeyDTO;
import io.joshuasalcedo.commandcenter.user.UserId;
import io.joshuasalcedo.commandcenter.user.api.UserService;
import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * Authenticated user endpoints for managing their own API keys.
 */
@RestController
@RequestMapping("/api/api-keys")
class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final UserService userService;

    ApiKeyController(ApiKeyService apiKeyService, UserService userService) {
        this.apiKeyService = apiKeyService;
        this.userService = userService;
    }

    @GetMapping
    List<ApiKeyDTO> list(@AuthenticationPrincipal OAuth2User principal) {
        return apiKeyService.findAllByOwner(resolveUserId(principal));
    }

    @PostMapping
    ApiKeyDTO create(@AuthenticationPrincipal OAuth2User principal,
                     @RequestBody CreateApiKeyRequest request) {
        UserId ownerId = resolveUserId(principal);
        if (request.expiresAt() != null) {
            return apiKeyService.create(ownerId, request.name(), request.expiresAt());
        }
        return apiKeyService.create(ownerId, request.name());
    }

    @PostMapping("/{id}/revoke")
    ResponseEntity<Void> revoke(@AuthenticationPrincipal OAuth2User principal,
                                @PathVariable String id) {
        apiKeyService.revoke(resolveUserId(principal), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@AuthenticationPrincipal OAuth2User principal,
                                @PathVariable String id) {
        apiKeyService.delete(resolveUserId(principal), id);
        return ResponseEntity.noContent().build();
    }

    private UserId resolveUserId(OAuth2User principal) {
        String googleId = principal.getAttribute("sub");
        UserDTO user = userService.findByGoogleId(googleId)
                .orElseThrow(() -> new IllegalStateException("authenticated user not found"));
        return user.id();
    }

    record CreateApiKeyRequest(String name, Instant expiresAt) {}
}
