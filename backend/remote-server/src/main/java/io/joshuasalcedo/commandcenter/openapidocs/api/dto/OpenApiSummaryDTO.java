package io.joshuasalcedo.commandcenter.openapidocs.api.dto;

import java.util.List;

public record OpenApiSummaryDTO(
        String title,
        String version,
        String description,
        int totalEndpoints,
        List<String> tags
) {
}