package io.joshuasalcedo.devcom.settings;

import io.joshuasalcedo.devcom.settings.DataExportService.DataExport;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

@RestController
@RequestMapping("/api/settings")
class SettingsController {

    private static final Path CONFIG_DIR = Path.of(System.getProperty("user.home"), ".devcom");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.properties");

    private final Environment environment;
    private final DataExportService dataExportService;

    SettingsController(Environment environment, DataExportService dataExportService) {
        this.environment = environment;
        this.dataExportService = dataExportService;
    }

    @GetMapping("/datasource")
    DatasourceInfo getDatasource() {
        String[] profiles = environment.getActiveProfiles();
        DatasourceProfile active = Arrays.stream(profiles)
                .filter(p -> isKnownProfile(p))
                .findFirst()
                .map(DatasourceProfile::valueOf)
                .orElse(DatasourceProfile.dev);

        DatasourceProfile pending = readPendingProfile();
        boolean hasPendingImport = dataExportService.hasPendingImport();

        return new DatasourceInfo(
                active,
                pending,
                pending != null && pending != active,
                hasPendingImport
        );
    }

    @PutMapping("/datasource")
    DatasourceInfo switchDatasource(@RequestBody SetDatasourceRequest request) throws IOException {
        // 1. Export current data
        dataExportService.exportData();

        // 2. Write the new profile
        Files.createDirectories(CONFIG_DIR);
        var props = new Properties();
        props.setProperty("spring.profiles.active", request.profile().name());
        try (var out = Files.newOutputStream(CONFIG_FILE)) {
            props.store(out, "devcom settings - restart required");
        }

        return getDatasource();
    }

    @PostMapping("/datasource/import")
    ResponseEntity<DataExport> importData() {
        DataExport result = dataExportService.importData();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/datasource/export")
    ResponseEntity<DataExport> exportData() throws IOException {
        DataExport result = dataExportService.exportData();
        return ResponseEntity.ok(result);
    }

    private DatasourceProfile readPendingProfile() {
        if (!Files.exists(CONFIG_FILE)) return null;
        try (var in = Files.newInputStream(CONFIG_FILE)) {
            var props = new Properties();
            props.load(in);
            String value = props.getProperty("spring.profiles.active");
            return value != null ? DatasourceProfile.valueOf(value) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isKnownProfile(String profile) {
        try {
            DatasourceProfile.valueOf(profile);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    record DatasourceInfo(
            DatasourceProfile active,
            DatasourceProfile pending,
            boolean restartRequired,
            boolean hasPendingImport
    ) {}
    record SetDatasourceRequest(DatasourceProfile profile) {}
}
