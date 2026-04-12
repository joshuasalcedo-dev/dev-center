package io.joshuasalcedo.commandcenter.artifacts;

import io.joshuasalcedo.commandcenter.ResourceNotFoundException;
import io.joshuasalcedo.commandcenter.artifacts.api.ArtifactService;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ArtifactDTO;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.PublishReleaseRequest;
import io.joshuasalcedo.commandcenter.artifacts.api.dto.ReleaseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
class DefaultArtifactService implements ArtifactService {

    private final ArtifactRepository repository;

    DefaultArtifactService(ArtifactRepository repository) {
        this.repository = repository;
    }

    @Override
    public ArtifactDTO create(String artifactName) {
        if (repository.existsByArtifactName(artifactName)) {
            throw new IllegalStateException("artifact '" + artifactName + "' already exists");
        }
        Artifact saved = repository.save(new Artifact(ArtifactId.create(), artifactName));
        return ArtifactMapper.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ArtifactDTO findById(String id) {
        return ArtifactMapper.from(loadOrThrow(ArtifactId.of(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtifactDTO> findByName(String artifactName) {
        return repository.findByArtifactName(artifactName)
                .map(ArtifactMapper::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtifactDTO> findAll() {
        return repository.findAll().stream()
                .map(ArtifactMapper::from)
                .toList();
    }

    @Override
    public ReleaseDTO publishRelease(String artifactId, PublishReleaseRequest request) {
        Artifact artifact = loadOrThrow(ArtifactId.of(artifactId));
        ArtifactVersion artifactVersion = ArtifactVersion.parse(request.version());

        Map<Platform, ArtifactBinary> binaries = new EnumMap<>(Platform.class);
        for (PublishReleaseRequest.BinaryEntry entry : request.binaries()) {
            Platform platform = Platform.valueOf(entry.platform());
            Hash hash = new Hash(Hash.Algorithm.valueOf(entry.hashAlgorithm()), entry.hashValue());
            ArtifactBinary binary = new ArtifactBinary(platform, entry.downloadUrl(), hash, entry.sizeBytes(), entry.signature());
            binaries.put(platform, binary);
        }

        Release release = artifact.publishRelease(artifactVersion, binaries);
        repository.save(artifact);
        return ArtifactMapper.fromRelease(release);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReleaseDTO> latestRelease(String artifactId) {
        Artifact artifact = loadOrThrow(ArtifactId.of(artifactId));
        return artifact.latestRelease().map(ArtifactMapper::fromRelease);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReleaseDTO> findRelease(String artifactId, String version) {
        Artifact artifact = loadOrThrow(ArtifactId.of(artifactId));
        return artifact.releaseOf(ArtifactVersion.parse(version)).map(ArtifactMapper::fromRelease);
    }

    @Override
    public void delete(String id) {
        ArtifactId artifactId = ArtifactId.of(id);
        if (!repository.existsById(artifactId)) {
            throw new ResourceNotFoundException("artifact not found: " + artifactId.value());
        }
        repository.deleteById(artifactId);
    }

    private Artifact loadOrThrow(ArtifactId id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("artifact not found: " + id.value()));
    }
}
