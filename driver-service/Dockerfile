FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/driver-service-0.0.1-SNAPSHOT.jar driver-service.jar
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "driver-service.jar"]
