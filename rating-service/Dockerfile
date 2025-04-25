FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/rating-service-0.0.1-SNAPSHOT.jar rating-service.jar
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "rating-service.jar"]
