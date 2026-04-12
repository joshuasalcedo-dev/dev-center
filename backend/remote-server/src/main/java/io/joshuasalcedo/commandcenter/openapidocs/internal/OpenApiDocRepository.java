package io.joshuasalcedo.commandcenter.openapidocs.internal;

import io.joshuasalcedo.commandcenter.openapidocs.OpenApiDoc;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:17 AM
 */
public interface OpenApiDocRepository extends JpaRepository<OpenApiDoc, Long> {
}
