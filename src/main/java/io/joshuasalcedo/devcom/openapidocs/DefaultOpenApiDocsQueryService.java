package io.joshuasalcedo.devcom.openapidocs;

import io.joshuasalcedo.devcom.openapidocs.api.OpenApiDocsQueryService;
import io.joshuasalcedo.devcom.openapidocs.api.dto.EndpointDTO;
import io.joshuasalcedo.devcom.openapidocs.api.dto.OpenApiDocSummary;
import io.joshuasalcedo.devcom.openapidocs.api.dto.OpenApiSummaryDTO;
import io.joshuasalcedo.devcom.openapidocs.internal.OpenApiDocRepository;
import io.joshuasalcedo.devcom.openapidocs.internal.OpenApiDocsException;
import io.joshuasalcedo.devcom.openapidocs.internal.OpenApiMapperService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026
 */
@Service
@Transactional(readOnly = true)
class DefaultOpenApiDocsQueryService implements OpenApiDocsQueryService {

    private final OpenApiDocRepository repository;
    private final OpenApiMapperService mapperService;

    DefaultOpenApiDocsQueryService(OpenApiDocRepository repository,
                                   OpenApiMapperService mapperService) {
        this.repository = repository;
        this.mapperService = mapperService;
    }

    @Override
    @Cacheable(value = "openapidocs-summary", key = "#id.value()")
    public OpenApiSummaryDTO getSummary(OpenApiDocId id) {
        OpenApiDoc doc = findDoc(id.value());
        return mapperService.parseSummary(doc);
    }

    @Override
    @Cacheable(value = "openapidocs-endpoints", key = "#id.value()")
    public EndpointDTO getEndpoints(OpenApiDocId id) {
        OpenApiDoc doc = findDoc(id.value());
        return mapperService.parseEndpoints(doc);
    }

    @Override
    public List<OpenApiDocSummary> listAll() {
        return repository.findAll().stream()
                .map(doc -> new OpenApiDocSummary(
                        doc.id().value(),
                        doc.serviceName(),
                        doc.rawJson().version(),
                        null
                ))
                .toList();
    }

    @Override
    public String getRawJson(OpenApiDocId id) {
        OpenApiDoc doc = findDoc(id.value());
        return doc.rawJson().value();
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @CacheEvict(value = {"openapidocs-summary", "openapidocs-endpoints"}, key = "#event.id().value()")
    void onOpenApiDocUpdated(OpenApiDocUpdatedEvent event) {
        // cache eviction handled by @CacheEvict
    }

    private OpenApiDoc findDoc(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OpenApiDocsException("OpenApiDoc not found: " + id));
    }
}
