package io.joshuasalcedo.commandcenter.artifacts;

record ArtifactReleasePublishedEvent(
        ArtifactId artifactId,
        ReleaseId releaseId,
        ArtifactVersion artifactVersion) {}

