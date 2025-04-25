FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/payment-service-0.0.1-SNAPSHOT.jar payment-service.jar
EXPOSE 8086

ENTRYPOINT ["java", "-jar", "payment-service.jar"]
