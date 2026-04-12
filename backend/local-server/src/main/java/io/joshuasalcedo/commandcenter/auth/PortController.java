package io.joshuasalcedo.commandcenter.auth;

import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PortController {

    private final ServletWebServerApplicationContext context;

    PortController(ServletWebServerApplicationContext context) {
        this.context = context;
    }

    @GetMapping("/port")
    int port() {
        return context.getWebServer().getPort();
    }
}
