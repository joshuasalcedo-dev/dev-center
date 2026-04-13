# Stage 1: Build the application
FROM bellsoft/liberica-openjdk-debian:25 AS build
WORKDIR /home/app

# Copy dependency files for better layer caching
COPY pom.xml .
# If using Maven wrapper: COPY mvnw . and COPY .mvn .mvn
# RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM bellsoft/liberica-openjre-debian:25-cds
WORKDIR /application

# Copy the built JAR from the builder stage
# Replace 'your-app-name.jar' with your actual JAR name from the target folder
COPY --from=build /home/app/target/*.jar application.jar

# Run as a non-root user for better security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Standard Spring Boot port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
