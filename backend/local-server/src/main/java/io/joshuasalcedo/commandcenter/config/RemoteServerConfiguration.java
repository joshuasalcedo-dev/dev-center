package io.joshuasalcedo.commandcenter.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.net.URI;

/**
 * @author JoshuaSalcedo
 * @since 4/12/2026 10:11 PM
 */
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(value = { RemoteServerPropery.class})
public class RemoteServerConfiguration {


	@Bean
	public RemoteServer remoteServer(RemoteServerPropery remoteServerPropery){
		try {
			return new RemoteServer(URI.create(remoteServerPropery.url()).toURL());
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}