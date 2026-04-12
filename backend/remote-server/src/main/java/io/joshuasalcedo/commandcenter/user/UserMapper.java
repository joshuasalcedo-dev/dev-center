package io.joshuasalcedo.commandcenter.user;

import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;

class UserMapper {

    static UserDTO from(User entity) {
        return new UserDTO(
                entity.id(),
                entity.name(),
                entity.email(),
                entity.picture(),
                entity.role(),
                entity.createdAt(),
                entity.lastLoginAt()
        );
    }
}
