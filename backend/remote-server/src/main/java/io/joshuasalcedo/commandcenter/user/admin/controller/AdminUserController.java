package io.joshuasalcedo.commandcenter.user.admin.controller;

import io.joshuasalcedo.commandcenter.user.api.UserService;
import io.joshuasalcedo.commandcenter.user.api.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin endpoints for monitoring users.
 * Requires ROLE_ADMIN (OAuth2 session).
 */
@RestController
@RequestMapping("/api/admin/users")
class AdminUserController {

    private final UserService userService;

    AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    List<UserDTO> listAll() {
        return userService.findAll();
    }
}
