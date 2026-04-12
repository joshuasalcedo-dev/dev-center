package io.joshuasalcedo.commandcenter.openapidocs.api;


import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDocId;
import io.joshuasalcedo.commandcenter.openapidocs.api.command.CreateOpenApiDocsCommand;
import io.joshuasalcedo.commandcenter.openapidocs.api.command.UpdateOpenApiDocsCommand;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026
 */
public interface OpenApiDocsCommandService {

    OpenApiDocId create(CreateOpenApiDocsCommand command);

    void update(UpdateOpenApiDocsCommand command);
}
