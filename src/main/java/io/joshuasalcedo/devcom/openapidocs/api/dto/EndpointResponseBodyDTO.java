package io.joshuasalcedo.devcom.openapidocs.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record EndpointResponseBodyDTO(
        int statusCode,
        String mediaType,
        @Column(columnDefinition = "TEXT")
        String schemaJson,
        String description
) {
}