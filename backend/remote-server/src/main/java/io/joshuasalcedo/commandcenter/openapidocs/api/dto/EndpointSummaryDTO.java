package io.joshuasalcedo.commandcenter.openapidocs.api.dto;


import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDocId;

public record EndpointSummaryDTO(
        OpenApiDocId docId,
        int totalEndpoints
) {
}
