package io.joshuasalcedo.devcom.openapidocs;

import io.joshuasalcedo.devcom.openapidocs.api.OpenApiDocsCommandService;
import io.joshuasalcedo.devcom.openapidocs.api.command.CreateOpenApiDocsCommand;
import io.joshuasalcedo.devcom.openapidocs.api.command.UpdateOpenApiDocsCommand;
import io.joshuasalcedo.devcom.openapidocs.internal.OpenApiDocRepository;
import io.joshuasalcedo.devcom.openapidocs.internal.OpenApiDocsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026
 */
@Service
@Transactional
class DefaultOpenApiDocsCommandService implements OpenApiDocsCommandService {

    private final OpenApiDocRepository repository;

    DefaultOpenApiDocsCommandService(OpenApiDocRepository repository) {
        this.repository = repository;
    }

    @Override
    public OpenApiDocId create(CreateOpenApiDocsCommand command) {
        OpenApiDoc doc = OpenApiDoc.create(command.serviceName(), command.rawJson());
        OpenApiDoc saved = repository.save(doc);
        return saved.id();
    }

    @Override
    public void update(UpdateOpenApiDocsCommand command) {
        OpenApiDoc doc = repository.findById(command.id().value())
                .orElseThrow(() -> new OpenApiDocsException(
                        "OpenApiDoc not found: " + command.id().value()));
        doc.update(command.rawJson());
        repository.save(doc);
    }
}
