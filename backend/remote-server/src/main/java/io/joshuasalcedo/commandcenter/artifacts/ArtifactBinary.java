package io.joshuasalcedo.commandcenter.artifacts;

import jakarta.persistence.*;

import java.net.URI;
import java.util.Objects;

@Embeddable
class ArtifactBinary {

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, insertable = false, updatable = false)
    private Platform platform;

    @Column(name = "download_url", nullable = false, length = 1000)
    private URI downloadUrl;

    @Embedded
    private Hash hash;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "signature", length = 2000)
    private String signature;

    protected ArtifactBinary() { /* JPA */ }

    public ArtifactBinary(Platform platform, URI downloadUrl, Hash hash, long sizeBytes, String signature) {
        if (platform == null)    throw new IllegalArgumentException("platform required");
        if (downloadUrl == null) throw new IllegalArgumentException("downloadUrl required");
        if (hash == null)        throw new IllegalArgumentException("hash required");
        if (sizeBytes <= 0)      throw new IllegalArgumentException("sizeBytes must be > 0");
        this.platform = platform;
        this.downloadUrl = downloadUrl;
        this.hash = hash;
        this.sizeBytes = sizeBytes;
        this.signature = signature;
    }

    public Platform platform()   { return platform; }
    public URI downloadUrl()     { return downloadUrl; }
    public Hash hash()           { return hash; }
    public long sizeBytes()      { return sizeBytes; }
    public String signature()    { return signature; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtifactBinary other)) return false;
        return sizeBytes == other.sizeBytes
            && platform == other.platform
            && Objects.equals(downloadUrl, other.downloadUrl)
            && Objects.equals(hash, other.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, downloadUrl, hash, sizeBytes);
    }
}