package io.joshuasalcedo.commandcenter.openapidocs.api.command;

import io.joshuasalcedo.commandcenter.user.UserId;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:38 AM
 */
public record CreateOpenApiDocsCommand(UserId ownerId, String serviceName, String rawJson) {}
