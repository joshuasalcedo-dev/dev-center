package io.joshuasalcedo.devcom.openapidocs.internal;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:41 AM
 */
public class OpenApiDocsException extends RuntimeException{
 public OpenApiDocsException() {
 }

 public OpenApiDocsException(String message) {
  super(message);
 }

 public OpenApiDocsException(String message, Throwable cause) {
  super(message, cause);
 }

 public OpenApiDocsException(Throwable cause) {
  super(cause);
 }

 public OpenApiDocsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
  super(message, cause, enableSuppression, writableStackTrace);
 }
}
