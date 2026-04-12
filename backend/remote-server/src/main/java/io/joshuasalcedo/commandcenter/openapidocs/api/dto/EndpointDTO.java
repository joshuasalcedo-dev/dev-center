package io.joshuasalcedo.commandcenter.openapidocs.api.dto;


import java.util.List;

public record EndpointDTO(
        EndpointSummaryDTO summary,
        List<EndpointItemDTO> endpoints
) {
}
