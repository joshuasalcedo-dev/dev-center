package io.joshuasalcedo.devcom.openapidocs.controller;

import io.joshuasalcedo.devcom.openapidocs.OpenApiDocId;
import io.joshuasalcedo.devcom.openapidocs.api.OpenApiDocsCommandService;
import io.joshuasalcedo.devcom.openapidocs.api.OpenApiDocsQueryService;
import io.joshuasalcedo.devcom.openapidocs.api.command.CreateOpenApiDocsCommand;
import io.joshuasalcedo.devcom.openapidocs.api.command.UpdateOpenApiDocsCommand;
import io.joshuasalcedo.devcom.openapidocs.api.dto.EndpointDTO;
import io.joshuasalcedo.devcom.openapidocs.api.dto.OpenApiDocSummary;
import io.joshuasalcedo.devcom.openapidocs.api.dto.OpenApiSummaryDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/openapi-docs")
class OpenApiDocsController {

    private final OpenApiDocsCommandService commandService;
    private final OpenApiDocsQueryService queryService;

    OpenApiDocsController(OpenApiDocsCommandService commandService, OpenApiDocsQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
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
    ResponseEntity<Void> create(@RequestBody CreateOpenApiDocsCommand command) {
        OpenApiDocId docId = commandService.create(command);
        return ResponseEntity.created(URI.create("/api/openapi-docs/" + docId.value())).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateRawJsonRequest request) {
        commandService.update(new UpdateOpenApiDocsCommand(new OpenApiDocId(id), request.rawJson()));
        return ResponseEntity.noContent().build();
    }

    record UpdateRawJsonRequest(String rawJson) {}
}
