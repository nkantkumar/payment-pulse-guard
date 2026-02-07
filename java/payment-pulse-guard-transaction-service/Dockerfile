# Payment Pulse Guard - Transaction Service (Java 24)
# Build from repo root: docker build -f java/payment-pulse-guard-transaction-service/Dockerfile java
FROM eclipse-temurin:24-jdk-alpine AS builder
WORKDIR /build

COPY pom.xml pom.xml
COPY payment-pulse-guard-api payment-pulse-guard-api
COPY payment-pulse-guard-transaction-service payment-pulse-guard-transaction-service

RUN mvn -q -pl payment-pulse-guard-api,payment-pulse-guard-transaction-service -am package -DskipTests

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

RUN adduser -D -u 1000 appuser
COPY --from=builder /build/payment-pulse-guard-transaction-service/target/*.jar app.jar
RUN chown appuser:appuser app.jar
USER appuser

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
