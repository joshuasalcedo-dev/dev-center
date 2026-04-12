package io.joshuasalcedo.devcom.openapidocs.internal;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/**
 * Validates that a String value is a parseable and valid OpenAPI specification
 * using the Swagger OpenAPIParser.
 *
 * @author JoshuaSalcedo
 * @since 4/7/2026
 */
public class OpenApiSpecValidator implements ConstraintValidator<OpenApiSpec, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            SwaggerParseResult result = new OpenAPIParser()
                    .readContents(value, null, null);

            if (result.getOpenAPI() == null) {
                addViolation(context, "Could not parse OpenAPI specification");
                return false;
            }

            List<String> messages = result.getMessages();
            if (messages != null && !messages.isEmpty()) {
                context.disableDefaultConstraintViolation();
                for (String message : messages) {
                    context.buildConstraintViolationWithTemplate(message)
                            .addConstraintViolation();
                }
                return false;
            }

            return true;
        } catch (Exception e) {
            addViolation(context, "Failed to parse OpenAPI specification: " + e.getMessage());
            return false;
        }
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
