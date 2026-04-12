package io.joshuasalcedo.devcom.openapidocs.api.dto;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:36 AM
 */
public record OpenApiDocSummary(
		Long id,
		String serviceName,
		Long version,
		String hash
) {}
