package io.joshuasalcedo.commandcenter.user.admin.controller;

import io.joshuasalcedo.commandcenter.artifacts.api.ArtifactService;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ArtifactDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.PublishReleaseRequest;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ReleaseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin endpoints for managing artifacts and publishing releases.
 * Requires ROLE_ADMIN (OAuth2 session).
 */
@RestController
@RequestMapping("/api/admin/artifacts")
class AdminArtifactController {

    private final ArtifactService artifactService;

    AdminArtifactController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @GetMapping
    List<ArtifactDTO> listAll() {
        return artifactService.findAll();
    }

    @PostMapping
    ArtifactDTO create(@RequestBody CreateArtifactRequest request) {
        return artifactService.create(request.name());
    }

    @GetMapping("/{id}")
    ArtifactDTO findById(@PathVariable String id) {
        return artifactService.findById(id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id) {
        artifactService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/releases")
    ReleaseDTO publishRelease(@PathVariable String id, @RequestBody PublishReleaseRequest request) {
        return artifactService.publishRelease(id, request);
    }

    @GetMapping("/{id}/releases/latest")
    ResponseEntity<ReleaseDTO> latestRelease(@PathVariable String id) {
        return artifactService.latestRelease(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/releases/{version}")
    ResponseEntity<ReleaseDTO> findRelease(@PathVariable String id, @PathVariable String version) {
        return artifactService.findRelease(id, version)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    record CreateArtifactRequest(String name) {}
}
