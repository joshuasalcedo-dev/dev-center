package io.joshuasalcedo.devcom.openapidocs.api.command;

import io.joshuasalcedo.devcom.openapidocs.OpenApiDocId;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:38 AM
 */
public record UpdateOpenApiDocsCommand(OpenApiDocId id, String rawJson) {}
