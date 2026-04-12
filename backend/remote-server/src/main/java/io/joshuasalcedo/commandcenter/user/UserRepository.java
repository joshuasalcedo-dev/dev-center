package io.joshuasalcedo.commandcenter.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, UserId> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByEmail(String email);
}
