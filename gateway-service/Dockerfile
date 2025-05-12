FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/gateway-service-0.0.1-SNAPSHOT.jar /app/gateway-service.jar
EXPOSE 9000

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]