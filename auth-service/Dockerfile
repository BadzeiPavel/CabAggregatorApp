FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/auth-service.jar
EXPOSE 8081

ENTRYPOINT ["java", "-Xmx512m", "-jar", "auth-service.jar"]