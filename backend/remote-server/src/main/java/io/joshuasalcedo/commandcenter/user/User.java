package io.joshuasalcedo.commandcenter.user;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "app_user")
class User {

    @EmbeddedId
    private UserId id;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "picture")
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    protected User() { /* JPA */ }

    User(String googleId, String name, String email, String picture, Role role) {
        if (googleId == null || googleId.isBlank()) {
            throw new IllegalArgumentException("googleId required");
        }
        this.id = UserId.create();
        this.googleId = googleId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.createdAt = Instant.now();
        this.lastLoginAt = Instant.now();
    }

    // --- Domain behavior ---

    void updateProfile(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.lastLoginAt = Instant.now();
    }

    void promoteToAdmin() {
        this.role = Role.ADMIN;
    }

    boolean isAdmin() {
        return role == Role.ADMIN;
    }

    // --- Accessors ---

    public UserId id()             { return id; }
    public String googleId()       { return googleId; }
    public String name()           { return name; }
    public String email()          { return email; }
    public String picture()        { return picture; }
    public Role role()             { return role; }
    public Instant createdAt()     { return createdAt; }
    public Instant lastLoginAt()   { return lastLoginAt; }
}
