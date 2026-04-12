package io.joshuasalcedo.commandcenter.artifacts.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TauriUpdateResponse(
        String version,
        String url,
        String signature,
        String notes,
        @JsonProperty("pub_date") Instant pubDate
) {
}
