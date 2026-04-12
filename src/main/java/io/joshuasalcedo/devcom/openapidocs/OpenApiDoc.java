package io.joshuasalcedo.devcom.openapidocs;


import io.joshuasalcedo.devcom.openapidocs.internal.OpenApiDocsException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;

/**
 * @author JoshuaSalcedo
 * @since 4/7/2026 2:04 AM
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
 public class OpenApiDoc extends AbstractAggregateRoot<OpenApiDoc> {

 @Id
 private Long id;

 private String serviceName;


  @jakarta.persistence.Embedded
  @AttributeOverrides(value = {
          @AttributeOverride(name = "version", column = @Column(name = "openapi_version")),
          @AttributeOverride(name = "value", column = @Column(name = "openapi_raw_json", columnDefinition = "TEXT")),
          @AttributeOverride(name = "lastUpdated", column = @Column(name = "openapi_last_updated"))
  })
  private OpenApiRawJson rawJson;
  public OpenApiDocId id() {
    return new OpenApiDocId(id);
  }


  void update(String rawJson) {
    Long version = this.rawJson.version();
    this.rawJson = this.rawJson.update(rawJson);
    if(this.rawJson.version() > version){
      registerEvent(OpenApiDocUpdatedEvent.from(this, version));
    }
  }

 public String serviceName() {
  return serviceName;
 }

 public OpenApiRawJson rawJson() {
  return rawJson;
 }

 static OpenApiDoc create(String serviceName, String rawJson) {
      if(serviceName == null || rawJson == null){
      throw new OpenApiDocsException("serviceName and rawJson cannot be null");
      }
      return OpenApiDoc.builder()
              .serviceName(serviceName)
              .rawJson(OpenApiRawJson.create(rawJson))
              .build();
 }
}