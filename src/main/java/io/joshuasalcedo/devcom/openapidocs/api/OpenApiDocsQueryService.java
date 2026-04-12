package io.joshuasalcedo.devcom.openapidocs.api;

import io.joshuasalcedo.devcom.openapidocs.OpenApiDocId;
import io.joshuasalcedo.devcom.openapidocs.api.dto.EndpointDTO;
import io.joshuasalcedo.devcom.openapidocs.api.dto.OpenApiDocSummary;
import io.joshuasalcedo.devcom.openapidocs.api.dto.OpenApiSummaryDTO;

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
