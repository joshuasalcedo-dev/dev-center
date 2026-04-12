package io.joshuasalcedo.commandcenter.openapidocs.api.dto;

import io.joshuasalcedo.commandcenter.openapidocs.api.dto.EndpointHttpMethod;

import java.util.List;

public record EndpointItemDTO(
        String path,
        EndpointHttpMethod method,
        String operationId,
        String summary,
        String description,
        List<EndpointParameter> parameters,
        EndpointRequestBodyDTO requestBody,
        List<EndpointResponseBodyDTO> responses,
        List<String> tags
) {
}
