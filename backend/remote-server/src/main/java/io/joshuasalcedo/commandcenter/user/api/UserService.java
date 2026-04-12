package io.joshuasalcedo.commandcenter.user.api;

import io.joshuasalcedo.commandcenter.user.UserId;
import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDTO upsertFromOAuth(String googleId, String name, String email, String picture);

    Optional<UserDTO> findByGoogleId(String googleId);

    Optional<UserDTO> findById(UserId id);

    List<UserDTO> findAll();
}
