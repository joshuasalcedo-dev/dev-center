package io.joshuasalcedo.devcom.openapidocs;

import io.joshuasalcedo.devcom.openapidocs.internal.OpenApiSpec;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:06 AM
 */
@Embeddable
public record OpenApiRawJson(

		Long version,
		@Column(columnDefinition = "TEXT")
		@OpenApiSpec
		String value,
		 Instant lastUpdated
) {


	public static OpenApiRawJson create(String rawJson) {
		return new OpenApiRawJson(1L, rawJson, Instant.now());
	}

	public OpenApiRawJson update(String rawJson) {
		if(rawJson.equalsIgnoreCase(this.value)){
			return this;
		} else{

			return new OpenApiRawJson(this.version + 1 ,rawJson, Instant.now());
		}
	}
}
