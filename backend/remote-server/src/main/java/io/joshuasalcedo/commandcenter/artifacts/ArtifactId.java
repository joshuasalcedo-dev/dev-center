package io.joshuasalcedo.commandcenter.artifacts;

import jakarta.persistence.Embeddable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author JoshuaSalcedo
 * @since 4/12/2026 10:24 PM
 */
@Embeddable
public record ArtifactId(

		String value
) {

	public static ArtifactId of(String value) {
		return new ArtifactId(value);
	}

	/**
	 * Creates a deterministic {@code ArtifactId} from the given input.
	 * The same input always yields the same UUID (RFC 4122 artifactVersion 3, MD5-based).
	 * Useful for idempotent artifact identification — e.g. deriving a stable ID
	 * from a Maven coordinate like {@code group:artifact:artifactVersion}.
	 */
	public static ArtifactId fromString(String input) {
		if (input == null) {
			throw new IllegalArgumentException("input must not be null");
		}
		UUID uuid = UUID.nameUUIDFromBytes(input.getBytes(StandardCharsets.UTF_8));
		return new ArtifactId(uuid.toString());
	}

	public static ArtifactId create(){
		return new ArtifactId(java.util.UUID.randomUUID().toString().replace("-", ""));
	}
}