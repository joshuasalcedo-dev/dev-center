package io.joshuasalcedo.commandcenter.openapidocs;


import io.joshuasalcedo.commandcenter.InvalidRequestException;
import io.joshuasalcedo.commandcenter.user.UserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

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

 @Embedded
 @AttributeOverride(name = "value", column = @Column(name = "owner_id", nullable = false, updatable = false))
 private UserId ownerId;

 private String serviceName;


  @Embedded
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

 public UserId ownerId() {
  return ownerId;
 }

 public String serviceName() {
  return serviceName;
 }

 public OpenApiRawJson rawJson() {
  return rawJson;
 }

 static OpenApiDoc create(UserId ownerId, String serviceName, String rawJson) {
      if(ownerId == null || serviceName == null || rawJson == null){
      throw new InvalidRequestException("ownerId, serviceName and rawJson cannot be null");
      }
      return OpenApiDoc.builder()
              .ownerId(ownerId)
              .serviceName(serviceName)
              .rawJson(OpenApiRawJson.create(rawJson))
              .build();
 }
}