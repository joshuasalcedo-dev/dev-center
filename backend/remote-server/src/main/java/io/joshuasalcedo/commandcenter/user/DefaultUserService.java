package io.joshuasalcedo.commandcenter.user;

import io.joshuasalcedo.commandcenter.user.api.UserService;
import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
class DefaultUserService implements UserService {

    private final UserRepository repository;
    private final Set<String> adminEmails;

    DefaultUserService(UserRepository repository,
                       @Value("${app.admin.emails:}") String adminEmails) {
        this.repository = repository;
        this.adminEmails = Stream.of(adminEmails.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public UserDTO upsertFromOAuth(String googleId, String name, String email, String picture) {
        boolean isAdmin = adminEmails.contains(email.toLowerCase());

        User user = repository.findByGoogleId(googleId).orElseGet(() -> {
            Role role = isAdmin ? Role.ADMIN : Role.USER;
            return new User(googleId, name, email, picture, role);
        });

        user.updateProfile(name, email, picture);

        if (isAdmin && !user.isAdmin()) {
            user.promoteToAdmin();
        }

        return UserMapper.from(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByGoogleId(String googleId) {
        return repository.findByGoogleId(googleId)
                .map(UserMapper::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(UserId id) {
        return repository.findById(id)
                .map(UserMapper::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return repository.findAll().stream()
                .map(UserMapper::from)
                .toList();
    }
}
