package io.joshuasalcedo.commandcenter.artifacts.controller;

import io.joshuasalcedo.commandcenter.artifacts.api.ArtifactService;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ArtifactBinaryDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ReleaseDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.UpdateCheckResponse;
import io.joshuasalcedo.commandcenter.config.cicd.Cicd;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Update check endpoint for the local-server.
 * Authenticated via API key (X-API-Key header).
 */
@RestController
@RequestMapping("/api/public/local")
class LocalArtifactController {

    private final Cicd cicd;
    private final ArtifactService artifactService;

    LocalArtifactController(Cicd cicd, ArtifactService artifactService) {
        this.cicd = cicd;
        this.artifactService = artifactService;
    }

    @GetMapping("/cicd/check")
    ResponseEntity<UpdateCheckResponse> checkUpdate() {
        String artifactId = cicd.localArtifact().id().value();

        Optional<ReleaseDTO> latest = artifactService.latestRelease(artifactId);
        if (latest.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        ReleaseDTO release = latest.get();

        // Local server is a JAR — pick the first available binary
        Optional<ArtifactBinaryDTO> binary = release.binaries().stream().findFirst();
        if (binary.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        ArtifactBinaryDTO b = binary.get();
        return ResponseEntity.ok(new UpdateCheckResponse(
                release.version(),
                b.downloadUrl(),
                b.hashAlgorithm(),
                b.hashValue(),
                b.sizeBytes()
        ));
    }
}
