package io.joshuasalcedo.devcom.openapidocs.api;

import io.joshuasalcedo.devcom.openapidocs.OpenApiDocId;
import io.joshuasalcedo.devcom.openapidocs.api.command.CreateOpenApiDocsCommand;
import io.joshuasalcedo.devcom.openapidocs.api.command.UpdateOpenApiDocsCommand;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026
 */
public interface OpenApiDocsCommandService {

    OpenApiDocId create(CreateOpenApiDocsCommand command);

    void update(UpdateOpenApiDocsCommand command);
}
