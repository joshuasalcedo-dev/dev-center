package io.joshuasalcedo.commandcenter.openapidocs.internal;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that the annotated String is a valid OpenAPI specification (JSON or YAML).
 *
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:43 AM
 */
@Documented
@Constraint(validatedBy = OpenApiSpecValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface OpenApiSpec {

    String message() default "Invalid OpenAPI specification";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
