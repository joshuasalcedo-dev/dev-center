package io.joshuasalcedo.commandcenter.artifacts.controller;

import io.joshuasalcedo.commandcenter.artifacts.Platform;
import io.joshuasalcedo.commandcenter.artifacts.api.ArtifactService;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ArtifactBinaryDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ReleaseDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.TauriUpdateResponse;
import io.joshuasalcedo.commandcenter.config.cicd.Cicd;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Public endpoint for Tauri desktop auto-updates.
 * No authentication required — Tauri verifies cicd integrity
 * via a signature baked into the app at build time.
 */
@RestController
@RequestMapping("/api/public/desktop")
class DesktopArtifactController {

    private final Cicd cicd;
    private final ArtifactService artifactService;

    DesktopArtifactController(Cicd cicd, ArtifactService artifactService) {
        this.cicd = cicd;
        this.artifactService = artifactService;
    }

    /**
     * Tauri updater endpoint.
     * Configure in tauri.conf.json:
     * {@code "endpoints": ["https://your-server/api/desktop/update/{{target}}/{{arch}}/{{current_version}}"]}
     *
     * @return 200 with Tauri JSON if cicd available, 204 if up to date
     */
    @GetMapping("/cicd/{target}/{arch}/{currentVersion}")
    ResponseEntity<TauriUpdateResponse> checkUpdate(@PathVariable String target,
                                                    @PathVariable String arch,
                                                    @PathVariable String currentVersion) {
        String artifactId = cicd.desktopArtifact().id().value();

        Optional<ReleaseDTO> latest = artifactService.latestRelease(artifactId);
        if (latest.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        ReleaseDTO release = latest.get();
        if (release.version().equals(currentVersion)) {
            return ResponseEntity.noContent().build();
        }

        Optional<Platform> platform = Platform.fromTauri(target, arch);
        if (platform.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Optional<ArtifactBinaryDTO> binary = release.binaries().stream()
                .filter(b -> b.platform().equals(platform.get().name()))
                .findFirst();

        if (binary.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        ArtifactBinaryDTO b = binary.get();
        return ResponseEntity.ok(new TauriUpdateResponse(
                release.version(),
                b.downloadUrl().toString(),
                b.signature(),
                null,
                release.publishedAt()
        ));
    }
}
