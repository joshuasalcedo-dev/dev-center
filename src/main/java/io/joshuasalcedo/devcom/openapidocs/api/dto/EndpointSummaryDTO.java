package io.joshuasalcedo.devcom.openapidocs.api.dto;

import io.joshuasalcedo.devcom.openapidocs.OpenApiDocId;

public record EndpointSummaryDTO(
        OpenApiDocId docId,
        int totalEndpoints
) {
}
