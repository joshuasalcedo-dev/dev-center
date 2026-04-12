package io.joshuasalcedo.devcom.openapidocs;

import org.jspecify.annotations.Nullable;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:13 AM
 */
public record OpenApiDocUpdatedEvent(
		OpenApiDocId id,
		String serviceName,
		Long oldVersion,
		Long newVersion
) {
	public static OpenApiDocUpdatedEvent from(OpenApiDoc doc,  Long oldVersion) {
		return new OpenApiDocUpdatedEvent(doc.id(), doc.serviceName(),  oldVersion, doc.rawJson().version());
	}

}
