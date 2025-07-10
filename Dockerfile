# Multi-stage build
FROM eclipse-temurin:21-jdk-alpine as builder

WORKDIR /app

# Copy gradle files
COPY build.gradle ./
COPY gradle/ ./gradle/
COPY gradlew ./

# Copy source code
COPY src/ ./src/

# Build the application
RUN ./gradlew clean build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S catalog && adduser -S catalog -G catalog

# Copy the built JAR
COPY --from=builder /app/build/libs/ecommerce-catalog-*.jar app.jar

# Create logs directory
RUN mkdir -p logs && chown catalog:catalog logs

# Expose port
EXPOSE 8080 8081

# Switch to non-root user
USER catalog

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/healthcheck || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "server", "application.yml"]