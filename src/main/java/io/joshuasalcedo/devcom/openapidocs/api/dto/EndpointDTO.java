package io.joshuasalcedo.devcom.openapidocs.api.dto;

import java.util.List;

public record EndpointDTO(
        EndpointSummaryDTO summary,
        List<EndpointItemDTO> endpoints
) {
}
