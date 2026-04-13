FROM bellsoft/liberica-openjre-debian:25-cds
WORKDIR /application

COPY remote-server/target/*.jar application.jar

RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
