package io.joshuasalcedo.commandcenter.cicd;

import io.joshuasalcedo.commandcenter.config.RemoteServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
class UpdateChecker {

	static final int EXIT_CODE_UPDATE_READY = 80;

	private static final Logger log = LoggerFactory.getLogger(UpdateChecker.class);

	private final BuildProperties buildProperties;
	private final ApplicationContext applicationContext;
	private final RestClient restClient;

	UpdateChecker(BuildProperties buildProperties,
	              RemoteServer remoteServer,
	              ApplicationContext applicationContext) {
		this.buildProperties = buildProperties;
		this.applicationContext = applicationContext;
		this.restClient = RestClient.builder()
				.baseUrl(remoteServer.url().toString())
				.build();
	}

	@EventListener(ApplicationReadyEvent.class)
	void checkForUpdate() {
		String currentVersion = buildProperties.getVersion();
		log.info("Current local-server version: {}", currentVersion);

		try {
			UpdateCheckResponse response = restClient.get()
					.uri("/api/public/local/cicd/check")
					.retrieve()
					.body(UpdateCheckResponse.class);

			if (response == null) {
				log.info("No update available.");
				return;
			}

			if (response.version().equals(currentVersion)) {
				log.info("Already running the latest version.");
				return;
			}

			log.info("New version available: {} (current: {})", response.version(), currentVersion);

			Path updatesDir = Path.of("updates");
			Files.createDirectories(updatesDir);
			Path downloaded = updatesDir.resolve("local-server-" + response.version());

			download(response.downloadUrl(), downloaded);
			verify(downloaded, response.hashAlgorithm(), response.hashValue(), response.sizeBytes());

			log.info("Update downloaded and verified: {}", downloaded);
			log.info("Exiting with code {} for wrapper script to apply update.", EXIT_CODE_UPDATE_READY);

			SpringApplication.exit(applicationContext, () -> EXIT_CODE_UPDATE_READY);

		} catch (Exception e) {
			log.warn("Update check failed, continuing with current version: {}", e.getMessage());
		}
	}

	private void download(URI uri, Path target) throws IOException {
		log.info("Downloading update from: {}", uri);

		byte[] bytes = restClient.get()
				.uri(uri)
				.retrieve()
				.body(byte[].class);

		if (bytes == null || bytes.length == 0) {
			throw new IOException("Empty response from download URL");
		}

		Files.write(target, bytes);
	}

	private void verify(Path file, String algorithm, String expectedHash, long expectedSize) throws IOException {
		long actualSize = Files.size(file);
		if (actualSize != expectedSize) {
			Files.delete(file);
			throw new IOException("Size mismatch: expected " + expectedSize + ", got " + actualSize);
		}

		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(Files.readAllBytes(file));
			String actualHash = HexFormat.of().formatHex(digest.digest());

			if (!actualHash.equalsIgnoreCase(expectedHash)) {
				Files.delete(file);
				throw new IOException("Hash mismatch: expected " + expectedHash + ", got " + actualHash);
			}
		} catch (java.security.NoSuchAlgorithmException e) {
			log.warn("Unknown hash algorithm '{}', skipping hash verification", algorithm);
		}
	}

	private record UpdateCheckResponse(
			String version,
			URI downloadUrl,
			String hashAlgorithm,
			String hashValue,
			long sizeBytes
	) {}
}
