# ============================================================================
# DOCKERFILE - TOTVS Integration Hub
# Multi-stage build for optimal image size and security
# ============================================================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy project files
COPY pom.xml .
COPY src ./src

# Build application
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="TOTVS Integration Team"
LABEL description="TOTVS Integration Hub - Enterprise Integration Platform"

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1000 appuser && adduser -u 1000 -G appuser -s /bin/sh -D appuser

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /build/target/integration-prototype-1.0.0-SNAPSHOT.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["java"]
CMD ["-Xmx512m", "-Xms256m", "-XX:+UseG1GC", "-jar", "app.jar"]

# ============================================================================
# Build instructions:
#   docker build -t totvs-integration-hub:latest .
#
# Run instructions:
#   docker run -d \
#     -p 8080:8080 \
#     -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/integration_hub \
#     -e SPRING_DATA_REDIS_HOST=host.docker.internal \
#     totvs-integration-hub:latest
# ============================================================================