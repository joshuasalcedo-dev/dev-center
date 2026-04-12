package io.joshuasalcedo.commandcenter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author JoshuaSalcedo
 * @since 4/12/2026 10:10 PM
 */
@ConfigurationProperties(prefix = "app.remote-server")
public record RemoteServerPropery(
		String url
) {
}
