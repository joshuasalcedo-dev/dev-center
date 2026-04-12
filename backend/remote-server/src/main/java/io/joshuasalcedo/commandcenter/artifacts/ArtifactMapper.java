package io.joshuasalcedo.commandcenter.artifacts;

import io.joshuasalcedo.commandcenter.artifacts.api.dto.ArtifactBinaryDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ArtifactDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ReleaseDTO;

import java.util.Comparator;
import java.util.List;

class ArtifactMapper {

    static ArtifactDTO from(Artifact entity) {
        List<ReleaseDTO> releases = entity.releases().stream()
                .sorted(Comparator.reverseOrder())
                .map(ArtifactMapper::fromRelease)
                .toList();

        return new ArtifactDTO(
                entity.id().value(),
                entity.artifactName(),
                releases
        );
    }

    static ReleaseDTO fromRelease(Release entity) {
        List<ArtifactBinaryDTO> binaries = entity.binaries().entrySet().stream()
                .map(e -> fromBinary(e.getKey(), e.getValue()))
                .toList();

        return new ReleaseDTO(
                entity.id().value().toString(),
                entity.version().toString(),
                entity.publishedAt(),
                binaries
        );
    }

    private static ArtifactBinaryDTO fromBinary(Platform platform, ArtifactBinary binary) {
        return new ArtifactBinaryDTO(
                platform.name(),
                binary.downloadUrl(),
                binary.hash().algorithm().name(),
                binary.hash().value(),
                binary.sizeBytes(),
                binary.signature()
        );
    }
}
