package io.joshuasalcedo.commandcenter.openapidocs.api;

import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDocId;
import io.joshuasalcedo.commandcenter.openapidocs.api.dto.EndpointDTO;
import io.joshuasalcedo.commandcenter.openapidocs.api.dto.OpenApiDocSummary;
import io.joshuasalcedo.commandcenter.openapidocs.api.dto.OpenApiSummaryDTO;

import java.util.List;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026
 */
public interface OpenApiDocsQueryService {

    OpenApiSummaryDTO getSummary(OpenApiDocId id);

    EndpointDTO getEndpoints(OpenApiDocId id);

    List<OpenApiDocSummary> listAll();

    String getRawJson(OpenApiDocId id);
}
