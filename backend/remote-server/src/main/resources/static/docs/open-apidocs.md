# OpenAPI Docs API - CI/CD Integration Guide

Base URL: `{{BASE_URL}}`

All authenticated endpoints require the `X-API-Key` header.
The API key identifies the user — uploaded docs are automatically owned by the key holder.

---

## Authentication

Every request must include your API key:

```
X-API-Key: cck_your_api_key_here
```

You can generate an API key from the web dashboard or request one from your admin.

---

## Endpoints

### 1. Upload a new OpenAPI spec

**`POST /api/openapi-docs`**

Upload a new OpenAPI specification for a service.

#### curl

```bash
curl -X POST {{BASE_URL}}/api/openapi-docs \
  -H "X-API-Key: cck_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "payment-service",
    "rawJson": "{\"openapi\":\"3.0.3\",\"info\":{\"title\":\"Payment Service\",\"version\":\"1.2.0\",\"description\":\"Handles payment processing\"},\"paths\":{\"/payments\":{\"post\":{\"operationId\":\"createPayment\",\"summary\":\"Create a payment\",\"tags\":[\"payments\"],\"requestBody\":{\"required\":true,\"content\":{\"application/json\":{\"schema\":{\"type\":\"object\",\"properties\":{\"amount\":{\"type\":\"number\"},\"currency\":{\"type\":\"string\"}}}}}},\"responses\":{\"201\":{\"description\":\"Payment created\"},\"400\":{\"description\":\"Invalid request\"}}}}}}"
  }'
```

#### Response

```
HTTP/1.1 201 Created
Location: /api/openapi-docs/1
```

#### Upload from a file (recommended for CI/CD)

```bash
# Using jq to embed the spec file as a JSON string
curl -X POST {{BASE_URL}}/api/openapi-docs \
  -H "X-API-Key: cck_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg name "payment-service" --arg spec "$(cat openapi.json)" \
    '{serviceName: $name, rawJson: $spec}')"
```

#### GitHub Actions

```yaml
name: Upload OpenAPI Docs

on:
  push:
    branches: [main]
    paths:
      - 'docs/openapi.json'

jobs:
  upload-docs:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Upload OpenAPI spec
        run: |
          curl -sf -X POST "${{ vars.COMMAND_CENTER_URL }}/api/openapi-docs" \
            -H "X-API-Key: ${{ secrets.COMMAND_CENTER_API_KEY }}" \
            -H "Content-Type: application/json" \
            -d "$(jq -n \
              --arg name "${{ github.event.repository.name }}" \
              --arg spec "$(cat docs/openapi.json)" \
              '{serviceName: $name, rawJson: $spec}')"
```

#### GitLab CI

```yaml
upload-openapi:
  stage: deploy
  image: alpine/curl
  script:
    - apk add --no-cache jq
    - |
      curl -sf -X POST "${COMMAND_CENTER_URL}/api/openapi-docs" \
        -H "X-API-Key: ${COMMAND_CENTER_API_KEY}" \
        -H "Content-Type: application/json" \
        -d "$(jq -n \
          --arg name "${CI_PROJECT_NAME}" \
          --arg spec "$(cat docs/openapi.json)" \
          '{serviceName: $name, rawJson: $spec}')"
  only:
    changes:
      - docs/openapi.json
```

---

### 2. Update an existing OpenAPI spec

**`PUT /api/openapi-docs/{id}`**

Replace the spec for an existing document. The version is auto-incremented if the content changed.

#### curl

```bash
curl -X PUT {{BASE_URL}}/api/openapi-docs/1 \
  -H "X-API-Key: cck_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d '{
    "rawJson": "{\"openapi\":\"3.0.3\",\"info\":{\"title\":\"Payment Service\",\"version\":\"1.3.0\",\"description\":\"Handles payment processing - updated\"},\"paths\":{\"/payments\":{\"post\":{\"operationId\":\"createPayment\",\"summary\":\"Create a payment\",\"tags\":[\"payments\"],\"requestBody\":{\"required\":true,\"content\":{\"application/json\":{\"schema\":{\"type\":\"object\",\"properties\":{\"amount\":{\"type\":\"number\"},\"currency\":{\"type\":\"string\"}}}}}},\"responses\":{\"201\":{\"description\":\"Payment created\"},\"400\":{\"description\":\"Invalid request\"}}}}}}"
  }'
```

#### Response

```
HTTP/1.1 204 No Content
```

#### Upload from a file

```bash
curl -X PUT {{BASE_URL}}/api/openapi-docs/1 \
  -H "X-API-Key: cck_your_api_key_here" \
  -H "Content-Type: application/json" \
  -d "$(jq -n --arg spec "$(cat openapi.json)" '{rawJson: $spec}')"
```

#### GitHub Actions (create or update)

A common CI/CD pattern: check if the doc exists, then create or update accordingly.

```yaml
name: Sync OpenAPI Docs

on:
  push:
    branches: [main]
    paths:
      - 'docs/openapi.json'

jobs:
  sync-docs:
    runs-on: ubuntu-latest
    env:
      API_URL: ${{ vars.COMMAND_CENTER_URL }}/api/openapi-docs
      API_KEY: ${{ secrets.COMMAND_CENTER_API_KEY }}
      SERVICE_NAME: ${{ github.event.repository.name }}
    steps:
      - uses: actions/checkout@v4

      - name: Find existing doc ID
        id: find
        run: |
          DOC_ID=$(curl -sf "$API_URL" \
            -H "X-API-Key: $API_KEY" \
            | jq -r ".[] | select(.serviceName == \"$SERVICE_NAME\") | .id // empty")
          echo "doc_id=$DOC_ID" >> "$GITHUB_OUTPUT"

      - name: Create new doc
        if: steps.find.outputs.doc_id == ''
        run: |
          curl -sf -X POST "$API_URL" \
            -H "X-API-Key: $API_KEY" \
            -H "Content-Type: application/json" \
            -d "$(jq -n \
              --arg name "$SERVICE_NAME" \
              --arg spec "$(cat docs/openapi.json)" \
              '{serviceName: $name, rawJson: $spec}')"

      - name: Update existing doc
        if: steps.find.outputs.doc_id != ''
        run: |
          curl -sf -X PUT "$API_URL/${{ steps.find.outputs.doc_id }}" \
            -H "X-API-Key: $API_KEY" \
            -H "Content-Type: application/json" \
            -d "$(jq -n --arg spec "$(cat docs/openapi.json)" '{rawJson: $spec}')"
```

---

### 3. List all OpenAPI docs

**`GET /api/openapi-docs`**

Returns a summary of all uploaded OpenAPI documents.

#### curl

```bash
curl -s {{BASE_URL}}/api/openapi-docs \
  -H "X-API-Key: cck_your_api_key_here" | jq .
```

#### Response

```json
[
  {
    "id": 1,
    "ownerId": "a1b2c3d4e5f6",
    "serviceName": "payment-service",
    "version": 3,
    "hash": null
  },
  {
    "id": 2,
    "ownerId": "a1b2c3d4e5f6",
    "serviceName": "user-service",
    "version": 1,
    "hash": null
  }
]
```

---

### 4. Get parsed summary

**`GET /api/openapi-docs/{id}/summary`**

Returns a parsed summary of the OpenAPI spec (title, version, description, tags, endpoint count).

#### curl

```bash
curl -s {{BASE_URL}}/api/openapi-docs/1/summary \
  -H "X-API-Key: cck_your_api_key_here" | jq .
```

#### Response

```json
{
  "title": "Payment Service",
  "version": "1.2.0",
  "description": "Handles payment processing",
  "totalEndpoints": 3,
  "tags": ["payments", "refunds"]
}
```

---

### 5. Get parsed endpoints

**`GET /api/openapi-docs/{id}/endpoints`**

Returns all parsed endpoints with parameters, request bodies, and responses.

#### curl

```bash
curl -s {{BASE_URL}}/api/openapi-docs/1/endpoints \
  -H "X-API-Key: cck_your_api_key_here" | jq .
```

#### Response

```json
{
  "summary": {
    "docId": { "value": 1 },
    "totalEndpoints": 2
  },
  "endpoints": [
    {
      "path": "/payments",
      "method": "POST",
      "operationId": "createPayment",
      "summary": "Create a payment",
      "description": null,
      "parameters": [],
      "requestBody": {
        "mediaType": "application/json",
        "schemaJson": "{\"type\":\"object\",\"properties\":{\"amount\":{\"type\":\"number\"},\"currency\":{\"type\":\"string\"}}}",
        "required": true
      },
      "responses": [
        {
          "statusCode": 201,
          "mediaType": null,
          "schemaJson": null,
          "description": "Payment created"
        },
        {
          "statusCode": 400,
          "mediaType": null,
          "schemaJson": null,
          "description": "Invalid request"
        }
      ],
      "tags": ["payments"]
    },
    {
      "path": "/payments/{id}",
      "method": "GET",
      "operationId": "getPayment",
      "summary": "Get a payment by ID",
      "description": "Retrieves payment details",
      "parameters": [
        {
          "name": "id",
          "location": "PATH",
          "type": "string",
          "required": true
        }
      ],
      "requestBody": null,
      "responses": [
        {
          "statusCode": 200,
          "mediaType": "application/json",
          "schemaJson": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\"},\"amount\":{\"type\":\"number\"},\"status\":{\"type\":\"string\"}}}",
          "description": "Payment found"
        },
        {
          "statusCode": 404,
          "mediaType": null,
          "schemaJson": null,
          "description": "Payment not found"
        }
      ],
      "tags": ["payments"]
    }
  ]
}
```

---

### 6. Get raw OpenAPI JSON

**`GET /api/openapi-docs/{id}/raw`**

Returns the raw OpenAPI spec JSON as-is. Useful for feeding into code generators or other tools.

#### curl

```bash
curl -s {{BASE_URL}}/api/openapi-docs/1/raw \
  -H "X-API-Key: cck_your_api_key_here" | jq .
```

#### Response

```json
{
  "openapi": "3.0.3",
  "info": {
    "title": "Payment Service",
    "version": "1.2.0",
    "description": "Handles payment processing"
  },
  "paths": {
    "/payments": {
      "post": {
        "operationId": "createPayment",
        "summary": "Create a payment",
        "tags": ["payments"],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "type": "object",
                "properties": {
                  "amount": { "type": "number" },
                  "currency": { "type": "string" }
                }
              }
            }
          }
        },
        "responses": {
          "201": { "description": "Payment created" },
          "400": { "description": "Invalid request" }
        }
      }
    }
  }
}
```

#### Download to a file

```bash
curl -s {{BASE_URL}}/api/openapi-docs/1/raw \
  -H "X-API-Key: cck_your_api_key_here" \
  -o openapi-downloaded.json
```

---

## CI/CD Recipes

### Spring Boot - Auto-export on build

Generate and upload the spec as part of your Maven build. This assumes you use `springdoc-openapi` and the app boots during tests.

```yaml
# GitHub Actions
name: Sync OpenAPI

on:
  push:
    branches: [main]

jobs:
  sync:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21

      - name: Build & extract spec
        run: |
          ./mvnw -q spring-boot:run &
          sleep 15
          curl -sf http://localhost:8080/v3/api-docs -o openapi.json
          kill %1

      - name: Upload to Command Center
        run: |
          DOC_ID=$(curl -sf "${{ vars.COMMAND_CENTER_URL }}/api/openapi-docs" \
            -H "X-API-Key: ${{ secrets.COMMAND_CENTER_API_KEY }}" \
            | jq -r ".[] | select(.serviceName == \"${{ github.event.repository.name }}\") | .id // empty")

          if [ -z "$DOC_ID" ]; then
            curl -sf -X POST "${{ vars.COMMAND_CENTER_URL }}/api/openapi-docs" \
              -H "X-API-Key: ${{ secrets.COMMAND_CENTER_API_KEY }}" \
              -H "Content-Type: application/json" \
              -d "$(jq -n \
                --arg name "${{ github.event.repository.name }}" \
                --arg spec "$(cat openapi.json)" \
                '{serviceName: $name, rawJson: $spec}')"
          else
            curl -sf -X PUT "${{ vars.COMMAND_CENTER_URL }}/api/openapi-docs/$DOC_ID" \
              -H "X-API-Key: ${{ secrets.COMMAND_CENTER_API_KEY }}" \
              -H "Content-Type: application/json" \
              -d "$(jq -n --arg spec "$(cat openapi.json)" '{rawJson: $spec}')"
          fi
```

### Code generation from stored spec

Pull a spec from Command Center and generate a client SDK.

```bash
# Download the spec
curl -s {{BASE_URL}}/api/openapi-docs/1/raw \
  -H "X-API-Key: cck_your_api_key_here" \
  -o payment-service.json

# Generate a TypeScript client
npx @openapitools/openapi-generator-cli generate \
  -i payment-service.json \
  -g typescript-axios \
  -o generated/payment-client
```

### Docker / docker-compose

```bash
docker run --rm \
  -e COMMAND_CENTER_URL={{BASE_URL}} \
  -e COMMAND_CENTER_API_KEY=cck_your_api_key_here \
  -v ./docs:/docs \
  alpine/curl sh -c '
    apk add --no-cache jq > /dev/null 2>&1
    curl -sf -X POST "${COMMAND_CENTER_URL}/api/openapi-docs" \
      -H "X-API-Key: ${COMMAND_CENTER_API_KEY}" \
      -H "Content-Type: application/json" \
      -d "$(jq -n \
        --arg name "my-service" \
        --arg spec "$(cat /docs/openapi.json)" \
        "{serviceName: \$name, rawJson: \$spec}")"
  '
```

---

## Error Responses

| Status | Meaning |
|--------|---------|
| `401 Unauthorized` | Missing or invalid `X-API-Key` header |
| `404 Not Found` | Document ID does not exist |
| `400 Bad Request` | Invalid OpenAPI spec (fails validation) |
| `204 No Content` | Update succeeded, or spec content is identical (no version bump) |
| `201 Created` | Document created, `Location` header contains the new resource URL |
