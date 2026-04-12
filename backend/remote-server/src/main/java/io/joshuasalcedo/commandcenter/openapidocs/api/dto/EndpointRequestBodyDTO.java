// === Value Objects ===

package io.joshuasalcedo.commandcenter.openapidocs.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record EndpointRequestBodyDTO(
        String mediaType,
        @Column(columnDefinition = "TEXT")
        String schemaJson,
        boolean required
) {
}