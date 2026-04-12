package io.joshuasalcedo.devcom.openapidocs.api.command;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:38 AM
 */
public record CreateOpenApiDocsCommand(String serviceName, String rawJson) {}
