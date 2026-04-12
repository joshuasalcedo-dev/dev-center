package io.joshuasalcedo.commandcenter.config.cicd;


import io.joshuasalcedo.commandcenter.artifacts.ArtifactId;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author JoshuaSalcedo
 * @since 4/13/2026 12:02 AM
 */
@Configuration
@EnableConfigurationProperties(value = { CicdProperty.class})
public class CicdConfig {

	@Bean
	public Cicd cicd(CicdProperty cicdPropery){
		CiCdArtifact local = new CiCdArtifact("local-server", ArtifactId.of(cicdPropery.localArtifactId()) );
		CiCdArtifact desktop = new CiCdArtifact("desktop-application", ArtifactId.of(cicdPropery.desktopArtifactId()) );
		return new Cicd(local, desktop);
	}
}