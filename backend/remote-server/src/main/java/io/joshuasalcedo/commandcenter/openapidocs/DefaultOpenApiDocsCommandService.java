package io.joshuasalcedo.commandcenter.openapidocs;


import io.joshuasalcedo.commandcenter.openapidocs.api.OpenApiDocsCommandService;
import io.joshuasalcedo.commandcenter.openapidocs.api.command.CreateOpenApiDocsCommand;
import io.joshuasalcedo.commandcenter.openapidocs.api.command.UpdateOpenApiDocsCommand;
import io.joshuasalcedo.commandcenter.openapidocs.internal.OpenApiDocRepository;
import io.joshuasalcedo.commandcenter.ResourceNotFoundException;
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
        OpenApiDoc doc = OpenApiDoc.create(command.ownerId(), command.serviceName(), command.rawJson());
        OpenApiDoc saved = repository.save(doc);
        return saved.id();
    }

    @Override
    public void update(UpdateOpenApiDocsCommand command) {
        OpenApiDoc doc = repository.findById(command.id().value())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "OpenApiDoc not found: " + command.id().value()));
        doc.update(command.rawJson());
        repository.save(doc);
    }
}
