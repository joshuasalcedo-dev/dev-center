# ── Build stage ──
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /workspace

# Copy Maven wrapper first
COPY .mvn .mvn
COPY mvnw ./
RUN chmod +x mvnw

# Copy parent pom and module poms for dependency caching
COPY pom.xml ./
COPY common/pom.xml common/
COPY local-server/pom.xml local-server/
COPY remote-server/pom.xml remote-server/

# Download dependencies (cached unless poms change)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -pl remote-server -am -q 2>/dev/null || true

# Copy source
COPY common/src common/src
COPY remote-server/src remote-server/src

# Build the fat jar, skip tests
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -pl remote-server -am -DskipTests -q

# ── Runtime stage ──
FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

COPY --from=build /workspace/remote-server/target/remote-server.jar app.jar

RUN chown -R app:app /app
USER app

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --retries=5 --start-period=45s \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
