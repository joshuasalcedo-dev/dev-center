package io.joshuasalcedo.commandcenter.openapidocs.api.dto;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record EndpointParameter(
        String name,
        @Enumerated(EnumType.STRING)
        EndpointParameterLocation location,
        String type,
        boolean required
) {
}