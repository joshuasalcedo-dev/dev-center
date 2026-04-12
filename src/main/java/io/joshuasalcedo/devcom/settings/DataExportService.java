package io.joshuasalcedo.devcom.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.joshuasalcedo.devcom.openapidocs.OpenApiDocId;
import io.joshuasalcedo.devcom.openapidocs.api.OpenApiDocsCommandService;
import io.joshuasalcedo.devcom.openapidocs.api.OpenApiDocsQueryService;
import io.joshuasalcedo.devcom.openapidocs.api.command.CreateOpenApiDocsCommand;
import io.joshuasalcedo.devcom.openapidocs.api.dto.OpenApiDocSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
class DataExportService {

    private static final Path EXPORT_FILE = Path.of(
            System.getProperty("user.home"), ".devcom", "export.json"
    );

    private final OpenApiDocsQueryService queryService;
    private final OpenApiDocsCommandService commandService;
    private final Gson gson;

    DataExportService(OpenApiDocsQueryService queryService, OpenApiDocsCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }

    DataExport exportData() throws IOException {
        List<OpenApiDocSummary> docs = queryService.listAll();

        List<OpenApiDocExport> exported = docs.stream()
                .map(doc -> new OpenApiDocExport(
                        doc.serviceName(),
                        queryService.getRawJson(new OpenApiDocId(doc.id())),
                        doc.version()
                ))
                .toList();

        var export = new DataExport(Instant.now(), exported.size(), exported);

        Files.createDirectories(EXPORT_FILE.getParent());
        Files.writeString(EXPORT_FILE, gson.toJson(export));

        return export;
    }

    @Transactional
    DataExport importData() {
        if (!Files.exists(EXPORT_FILE)) {
            throw new IllegalStateException("No export file found at " + EXPORT_FILE);
        }

        DataExport data;
        try {
            data = gson.fromJson(Files.readString(EXPORT_FILE), DataExport.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read export file", e);
        }

        for (OpenApiDocExport doc : data.openApiDocs()) {
            commandService.create(new CreateOpenApiDocsCommand(doc.serviceName(), doc.rawJson()));
        }

        // Remove export file after successful import
        try {
            Files.delete(EXPORT_FILE);
        } catch (IOException e) {
            // not critical
        }

        return data;
    }

    boolean hasPendingImport() {
        return Files.exists(EXPORT_FILE);
    }

    record DataExport(Instant exportedAt, int totalRecords, List<OpenApiDocExport> openApiDocs) {}
    record OpenApiDocExport(String serviceName, String rawJson, Long version) {}

    private static class InstantAdapter extends TypeAdapter<Instant> {
        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            out.value(value != null ? value.toString() : null);
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            String value = in.nextString();
            return value != null ? Instant.parse(value) : null;
        }
    }
}
