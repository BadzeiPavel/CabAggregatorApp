FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/passenger-service-0.0.1-SNAPSHOT.jar passenger-service.jar
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "passenger-service.jar"]
