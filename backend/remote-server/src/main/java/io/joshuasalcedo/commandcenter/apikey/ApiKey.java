package io.joshuasalcedo.commandcenter.apikey;

import io.joshuasalcedo.commandcenter.user.UserId;
import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Entity
@Table(name = "api_key")
class ApiKey {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final String PREFIX = "cck_"; // commandcenter key
    private static final int RANDOM_BYTES = 32;  // 256 bits of entropy

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 64)
    private String id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "owner_id", nullable = false, updatable = false, length = 64))
    private UserId ownerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    protected ApiKey() { /* JPA */ }

    ApiKey(UserId owner, String name) {
        this(owner, name, null);
    }

    ApiKey(UserId owner, String name, Instant expiresAt) {
        if (owner == null) {
            throw new IllegalArgumentException("owner required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        this.id = generateId();
        this.ownerId = owner;
        this.name = name;
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    static String generateId() {
        byte[] bytes = new byte[RANDOM_BYTES];
        RANDOM.nextBytes(bytes);
        return PREFIX + ENCODER.encodeToString(bytes);
    }

    // --- Domain behavior ---

    boolean isValid() {
        if (revoked) return false;
        return expiresAt == null || !Instant.now().isAfter(expiresAt);
    }

    boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    void revoke() {
        this.revoked = true;
    }

    void markUsed() {
        this.lastUsedAt = Instant.now();
    }

    // --- Accessors ---

    public String id()            { return id; }
    public UserId ownerId()       { return ownerId; }
    public String name()          { return name; }
    public Instant createdAt()    { return createdAt; }
    public Instant expiresAt()    { return expiresAt; }
    public boolean isRevoked()    { return revoked; }
    public Instant lastUsedAt()   { return lastUsedAt; }
}
