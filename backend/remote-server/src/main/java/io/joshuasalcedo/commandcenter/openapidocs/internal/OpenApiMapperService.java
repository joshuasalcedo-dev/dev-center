package io.joshuasalcedo.commandcenter.openapidocs.internal;

import com.google.gson.Gson;
import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDoc;
import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDocId;
import io.joshuasalcedo.commandcenter.openapidocs.api.dto.*;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenApiMapperService {

    private final Gson gson = new Gson();

    public OpenApiSummaryDTO parseSummary(OpenApiDoc doc) {
        OpenAPI openApi = doParse(doc.rawJson().value());

        List<String> tags = openApi.getTags() != null
                ? openApi.getTags().stream().map(t -> t.getName()).toList()
                : List.of();

        int totalEndpoints = openApi.getPaths() != null
                ? openApi.getPaths().values().stream()
                    .mapToInt(this::countOperations)
                    .sum()
                : 0;

        return new OpenApiSummaryDTO(
                openApi.getInfo() != null ? openApi.getInfo().getTitle() : null,
                openApi.getInfo() != null ? openApi.getInfo().getVersion() : null,
                openApi.getInfo() != null ? openApi.getInfo().getDescription() : null,
                totalEndpoints,
                tags
        );
    }

    public EndpointDTO parseEndpoints(OpenApiDoc doc) {
        OpenAPI openApi = doParse(doc.rawJson().value());
        OpenApiDocId docId = doc.id();

        List<EndpointItemDTO> items = mapEndpointItems(openApi);
        EndpointSummaryDTO summary = new EndpointSummaryDTO(docId, items.size());

        return new EndpointDTO(summary, items);
    }

    private OpenAPI doParse(String rawJson) {
        OpenAPI openApi = new OpenAPIParser()
                .readContents(rawJson, null, null)
                .getOpenAPI();

        if (openApi == null) {
            throw new IllegalArgumentException("Failed to parse OpenAPI spec");
        }

        return openApi;
    }

    private int countOperations(PathItem pathItem) {
        int count = 0;
        if (pathItem.getGet() != null) count++;
        if (pathItem.getPost() != null) count++;
        if (pathItem.getPut() != null) count++;
        if (pathItem.getPatch() != null) count++;
        if (pathItem.getDelete() != null) count++;
        if (pathItem.getHead() != null) count++;
        if (pathItem.getOptions() != null) count++;
        return count;
    }

    private List<EndpointItemDTO> mapEndpointItems(OpenAPI openApi) {
        if (openApi.getPaths() == null) return List.of();

        List<EndpointItemDTO> items = new ArrayList<>();

        openApi.getPaths().forEach((path, pathItem) -> {
            mapOperation(path, EndpointHttpMethod.GET, pathItem.getGet()).ifPresent(items::add);
            mapOperation(path, EndpointHttpMethod.POST, pathItem.getPost()).ifPresent(items::add);
            mapOperation(path, EndpointHttpMethod.PUT, pathItem.getPut()).ifPresent(items::add);
            mapOperation(path, EndpointHttpMethod.PATCH, pathItem.getPatch()).ifPresent(items::add);
            mapOperation(path, EndpointHttpMethod.DELETE, pathItem.getDelete()).ifPresent(items::add);
            mapOperation(path, EndpointHttpMethod.HEAD, pathItem.getHead()).ifPresent(items::add);
            mapOperation(path, EndpointHttpMethod.OPTIONS, pathItem.getOptions()).ifPresent(items::add);
        });

        return items;
    }

    private Optional<EndpointItemDTO> mapOperation(String path, EndpointHttpMethod method, Operation operation) {
        if (operation == null) return Optional.empty();

        List<EndpointParameter> parameters = operation.getParameters() != null
                ? operation.getParameters().stream().map(this::mapParameter).toList()
                : List.of();

        EndpointRequestBodyDTO requestBody = operation.getRequestBody() != null
                ? mapRequestBody(operation.getRequestBody())
                : null;

        List<EndpointResponseBodyDTO> responses = operation.getResponses() != null
                ? mapResponses(operation.getResponses())
                : List.of();

        List<String> tags = operation.getTags() != null
                ? operation.getTags()
                : List.of();

        return Optional.of(new EndpointItemDTO(
                path,
                method,
                operation.getOperationId(),
                operation.getSummary(),
                operation.getDescription(),
                parameters,
                requestBody,
                responses,
                tags
        ));
    }

    private EndpointParameter mapParameter(Parameter parameter) {
        EndpointParameterLocation location = switch (parameter.getIn().toLowerCase()) {
            case "path" -> EndpointParameterLocation.PATH;
            case "query" -> EndpointParameterLocation.QUERY;
            case "header" -> EndpointParameterLocation.HEADER;
            case "cookie" -> EndpointParameterLocation.COOKIE;
            default -> throw new IllegalArgumentException("Unknown parameter location: " + parameter.getIn());
        };

        String type = parameter.getSchema() != null
                ? parameter.getSchema().getType()
                : "string";

        return new EndpointParameter(
                parameter.getName(),
                location,
                type,
                parameter.getRequired() != null && parameter.getRequired()
        );
    }

    private EndpointRequestBodyDTO mapRequestBody(io.swagger.v3.oas.models.parameters.RequestBody requestBody) {
        Content content = requestBody.getContent();
        if (content == null || content.isEmpty()) {
            return new EndpointRequestBodyDTO(null, null, requestBody.getRequired() != null && requestBody.getRequired());
        }

        Map.Entry<String, io.swagger.v3.oas.models.media.MediaType> entry = content.entrySet().iterator().next();
        String schemaJson = entry.getValue().getSchema() != null
                ? gson.toJson(entry.getValue().getSchema())
                : null;

        return new EndpointRequestBodyDTO(
                entry.getKey(),
                schemaJson,
                requestBody.getRequired() != null && requestBody.getRequired()
        );
    }

    private List<EndpointResponseBodyDTO> mapResponses(io.swagger.v3.oas.models.responses.ApiResponses responses) {
        List<EndpointResponseBodyDTO> result = new ArrayList<>();

        responses.forEach((statusCode, apiResponse) -> {
            int code;
            try {
                code = Integer.parseInt(statusCode);
            } catch (NumberFormatException e) {
                code = 0;
            }

            if (apiResponse.getContent() == null || apiResponse.getContent().isEmpty()) {
                result.add(new EndpointResponseBodyDTO(
                        code, null, null, apiResponse.getDescription()
                ));
                return;
            }

            Map.Entry<String, io.swagger.v3.oas.models.media.MediaType> entry =
                    apiResponse.getContent().entrySet().iterator().next();

            String schemaJson = entry.getValue().getSchema() != null
                    ? gson.toJson(entry.getValue().getSchema())
                    : null;

            result.add(new EndpointResponseBodyDTO(
                    code,
                    entry.getKey(),
                    schemaJson,
                    apiResponse.getDescription()
            ));
        });

        return result;
    }
}
