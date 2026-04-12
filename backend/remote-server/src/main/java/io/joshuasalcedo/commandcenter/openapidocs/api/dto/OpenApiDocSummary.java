package io.joshuasalcedo.commandcenter.openapidocs.api.dto;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:36 AM
 */
public record OpenApiDocSummary(
		Long id,
		String ownerId,
		String serviceName,
		Long version,
		String hash
) {}
