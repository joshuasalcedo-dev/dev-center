package io.joshuasalcedo.devcom.openapidocs.internal;

import io.joshuasalcedo.devcom.openapidocs.OpenApiDoc;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:17 AM
 */
public interface OpenApiDocRepository extends JpaRepository<OpenApiDoc, Long> {
}
