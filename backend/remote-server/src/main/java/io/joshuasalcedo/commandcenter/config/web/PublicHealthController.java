package io.joshuasalcedo.commandcenter.config.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author JoshuaSalcedo
 * @since 4/13/2026 8:41 AM
 */
@RestController
 class PublicHealthController {

    @GetMapping("/health")
    public HealthStatus health() {
        return HealthStatus.capture();
    }
}