package io.joshuasalcedo.commandcenter.clipboard;

/**
 * @author JoshuaSalcedo
 * @since 4/12/2026 8:22 PM
 */

public record ClipboardId(String value)  {
	public static ClipboardId of(String value) {
		return new ClipboardId(value);
	}

	public static ClipboardId create(){
		return new ClipboardId(java.util.UUID.randomUUID().toString().replace("-", ""));
	}
}
