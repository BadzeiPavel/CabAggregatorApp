FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/ride-service-0.0.1-SNAPSHOT.jar ride-service.jar
EXPOSE 8085

ENTRYPOINT ["java", "-jar", "ride-service.jar"]
