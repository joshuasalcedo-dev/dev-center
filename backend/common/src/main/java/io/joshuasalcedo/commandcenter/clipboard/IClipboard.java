package io.joshuasalcedo.commandcenter.clipboard;

import javax.sound.sampled.Clip;
import java.time.Instant;

/**
 * @author JoshuaSalcedo
 * @since 4/12/2026 8:41 PM
 */

public interface IClipboard {
	ClipboardId id();
	String content();
	Instant timestamp();
}
