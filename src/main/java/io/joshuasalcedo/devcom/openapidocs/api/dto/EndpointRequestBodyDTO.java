// === Value Objects ===

package io.joshuasalcedo.devcom.openapidocs.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record EndpointRequestBodyDTO(
        String mediaType,
        @Column(columnDefinition = "TEXT")
        String schemaJson,
        boolean required
) {
}