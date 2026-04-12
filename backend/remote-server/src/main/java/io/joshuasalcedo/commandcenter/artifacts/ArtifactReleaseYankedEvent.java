package io.joshuasalcedo.commandcenter.artifacts;


record ArtifactReleaseYankedEvent(
        ArtifactId artifactId,
        ReleaseId releaseId,
        ArtifactVersion artifactVersion) {}