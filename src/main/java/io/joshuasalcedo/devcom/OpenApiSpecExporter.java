package io.joshuasalcedo.devcom;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.web.server.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
class OpenApiSpecExporter {

    private final RestTemplate restTemplate;

    OpenApiSpecExporter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @EventListener
    void onServerReady(ServletWebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        String url = "http://localhost:" + port + "/v3/api-docs";

        try {
            String spec = restTemplate.getForObject(url, String.class);
            Path outputDir = Path.of("src-tauri", "binaries");
            Files.createDirectories(outputDir);
            Path outputFile = outputDir.resolve("openapi.json");
            Files.writeString(outputFile, spec);
            System.out.println("OpenAPI spec written to " + outputFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write OpenAPI spec: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to fetch OpenAPI spec: " + e.getMessage());
        }
    }
}
