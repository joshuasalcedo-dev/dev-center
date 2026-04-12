package io.joshuasalcedo.devcom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DevComApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevComApplication.class, args);
	}

	@EventListener
	void onServerStarted(ServletWebServerInitializedEvent event) {
		int port = event.getWebServer().getPort();
		System.out.println("SIDECAR_PORT=" + port);
		System.out.flush();
	}

}
