package io.joshuasalcedo.commandcenter.openapidocs.api.command;


import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDocId;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:38 AM
 */
public record UpdateOpenApiDocsCommand(OpenApiDocId id, String rawJson) {}
