package io.joshuasalcedo.commandcenter.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
class PortFileWriter {

    private static final Logger log = LoggerFactory.getLogger(PortFileWriter.class);
    private static final Path PORT_FILE = Path.of(
            System.getProperty("user.home"), ".dev-center", "port");

    @EventListener
    void onServerStarted(ServletWebServerInitializedEvent event) throws IOException {
        int port = event.getWebServer().getPort();
        Files.createDirectories(PORT_FILE.getParent());
        Files.writeString(PORT_FILE, String.valueOf(port));
        log.info("Local server port {} written to {}", port, PORT_FILE);
    }
}
