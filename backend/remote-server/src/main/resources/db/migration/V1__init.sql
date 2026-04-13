-- =============================================
-- V1: Initial schema
-- =============================================

CREATE TABLE IF NOT EXISTS app_user (
                                        id              VARCHAR(64)     NOT NULL PRIMARY KEY,
    google_id       VARCHAR(255)    NOT NULL UNIQUE,
    name            VARCHAR(255)    NOT NULL,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    picture         VARCHAR(255),
    role            VARCHAR(10)     NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    last_login_at   TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS api_key (
                                       id              VARCHAR(64)     NOT NULL PRIMARY KEY,
    owner_id        VARCHAR(64)     NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    expires_at      TIMESTAMP,
    revoked         BOOLEAN         NOT NULL DEFAULT FALSE,
    last_used_at    TIMESTAMP,
    CONSTRAINT fk_api_key_owner FOREIGN KEY (owner_id) REFERENCES app_user(id)
    );

CREATE TABLE IF NOT EXISTS artifact (
                                        artifact_id     VARCHAR(255)    NOT NULL PRIMARY KEY,
    artifact_name   VARCHAR(255)    NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS artifact_release (
                                                release_id      UUID            NOT NULL PRIMARY KEY,
                                                artifact_id     VARCHAR(255)    NOT NULL,
    version_major   INT             NOT NULL,
    version_minor   INT             NOT NULL,
    version_patch   INT             NOT NULL,
    published_at    TIMESTAMP       NOT NULL,
    CONSTRAINT fk_release_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(artifact_id),
    CONSTRAINT uq_release_artifact_version UNIQUE (artifact_id, version_major, version_minor, version_patch)
    );

CREATE TABLE IF NOT EXISTS release_binary (
                                              release_id      UUID            NOT NULL,
                                              platform        VARCHAR(255)    NOT NULL,
    download_url    VARCHAR(1000)   NOT NULL,
    algorithm       VARCHAR(10)     NOT NULL,
    hash_value      VARCHAR(128)    NOT NULL,
    size_bytes      BIGINT          NOT NULL,
    signature       VARCHAR(2000),
    PRIMARY KEY (release_id, platform),
    CONSTRAINT fk_binary_release FOREIGN KEY (release_id) REFERENCES artifact_release(release_id)
    );

CREATE TABLE IF NOT EXISTS open_api_doc (
                                            id                  BIGINT          NOT NULL PRIMARY KEY,
                                            owner_id            VARCHAR(64)     NOT NULL,
    service_name        VARCHAR(255),
    openapi_version     BIGINT,
    openapi_raw_json    TEXT,
    openapi_last_updated TIMESTAMP,
    CONSTRAINT fk_openapidoc_owner FOREIGN KEY (owner_id) REFERENCES app_user(id)
    );

-- =============================================
-- Spring Modulith event publication registry
-- =============================================

CREATE TABLE IF NOT EXISTS event_publication (
                                                 id                      UUID                        NOT NULL PRIMARY KEY,
                                                 listener_id             TEXT                        NOT NULL,
                                                 event_type              TEXT                        NOT NULL,
                                                 serialized_event        TEXT                        NOT NULL,
                                                 publication_date        TIMESTAMP WITH TIME ZONE    NOT NULL,
                                                 completion_date         TIMESTAMP WITH TIME ZONE,
                                                 last_resubmission_date  TIMESTAMP WITH TIME ZONE,
                                                 completion_attempts     INTEGER                     NOT NULL DEFAULT 0,
                                                 status                  VARCHAR(16)                 NOT NULL DEFAULT 'PUBLISHED'
    );

CREATE INDEX IF NOT EXISTS idx_event_publication_listener_id
    ON event_publication (listener_id);

CREATE INDEX IF NOT EXISTS idx_event_publication_incomplete
    ON event_publication (completion_date)
    WHERE completion_date IS NULL;