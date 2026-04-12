package io.joshuasalcedo.commandcenter.openapidocs.controller;

import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDocId;
import io.joshuasalcedo.commandcenter.openapidocs.api.OpenApiDocsCommandService;
import io.joshuasalcedo.commandcenter.openapidocs.api.OpenApiDocsQueryService;
import io.joshuasalcedo.commandcenter.openapidocs.api.command.CreateOpenApiDocsCommand;
import io.joshuasalcedo.commandcenter.openapidocs.api.command.UpdateOpenApiDocsCommand;
import io.joshuasalcedo.commandcenter.openapidocs.api.dto.EndpointDTO;
import io.joshuasalcedo.commandcenter.openapidocs.api.dto.OpenApiDocSummary;
import io.joshuasalcedo.commandcenter.openapidocs.api.dto.OpenApiSummaryDTO;
import io.joshuasalcedo.commandcenter.user.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/openapi-docs")
class OpenApiDocsController {

    private static final String BASE_URL_PLACEHOLDER = "{{BASE_URL}}";

    private final OpenApiDocsCommandService commandService;
    private final OpenApiDocsQueryService queryService;
    private final String baseUrl;

    OpenApiDocsController(OpenApiDocsCommandService commandService,
                          OpenApiDocsQueryService queryService,
                          @Value("${app.base-url}") String baseUrl) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.baseUrl = baseUrl;
    }

    @GetMapping(value = "/cicd-sample", produces = "text/markdown")
    String cicdSample() throws IOException {
        String template = new ClassPathResource("static/docs/open-apidocs.md")
                .getContentAsString(StandardCharsets.UTF_8);
        return template.replace(BASE_URL_PLACEHOLDER, baseUrl);
    }

    @GetMapping
    List<OpenApiDocSummary> listAll() {
        return queryService.listAll();
    }

    @GetMapping("/{id}/summary")
	OpenApiSummaryDTO getSummary(@PathVariable Long id) {
        return queryService.getSummary(new OpenApiDocId(id));
    }

    @GetMapping(value = "/{id}/raw", produces = MediaType.APPLICATION_JSON_VALUE)
    String getRawJson(@PathVariable Long id) {
        return queryService.getRawJson(new OpenApiDocId(id));
    }

    @GetMapping("/{id}/endpoints")
	EndpointDTO getEndpoints(@PathVariable Long id) {
        return queryService.getEndpoints(new OpenApiDocId(id));
    }

    @PostMapping
    ResponseEntity<Void> create(@AuthenticationPrincipal UserId ownerId,
                                @RequestBody CreateRequest request) {
        CreateOpenApiDocsCommand command = new CreateOpenApiDocsCommand(
                ownerId, request.serviceName(), request.rawJson());
        OpenApiDocId docId = commandService.create(command);
        return ResponseEntity.created(URI.create("/api/openapi-docs/" + docId.value())).build();
    }

    record CreateRequest(String serviceName, String rawJson) {}

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateRawJsonRequest request) {
        commandService.update(new UpdateOpenApiDocsCommand(new OpenApiDocId(id), request.rawJson()));
        return ResponseEntity.noContent().build();
    }

    record UpdateRawJsonRequest(String rawJson) {}
}
