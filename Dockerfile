# ==============================================================================
# DOCKERFILE DEFINITIVO - TOTVS INTEGRATION HUB
# ==============================================================================

# Stage 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar pom.xml primeiro (cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte e compilar
COPY src/ src/
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime otimizado
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="TOTVS Integration Team"
LABEL version="1.0.0"
LABEL description="TOTVS Integration Hub - Enterprise Integration Platform"

# Criar usuário não-root
RUN addgroup -g 1001 -S totvs && \
    adduser -u 1001 -S totvs -G totvs

# Instalar dependências
RUN apk add --no-cache curl tzdata netcat-openbsd && \
    rm -rf /var/cache/apk/*

# Configurar timezone
ENV TZ=America/Sao_Paulo

# Criar diretórios
RUN mkdir -p /app/logs /app/config /app/data && \
    chown -R totvs:totvs /app

WORKDIR /app

# Copiar JAR da aplicação
COPY --from=builder --chown=totvs:totvs /app/target/*.jar app.jar

# ==========================================
# CONFIGURAÇÕES FORÇADAS PARA RESOLVER O BUG
# ==========================================

# Configurações JVM otimizadas
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# FORÇAR configurações MongoDB e Redis via ENV
ENV SPRING_PROFILES_ACTIVE=production
ENV SERVER_PORT=8080

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Trocar para usuário não-root
USER totvs:totvs

# ==========================================
# ENTRYPOINT FORÇADO PARA RESOLVER O BUG
# ==========================================
ENTRYPOINT ["sh", "-c", "\
echo '🚀 Starting TOTVS Integration Hub...' && \
echo '📡 MongoDB URI: '$SPRING_DATA_MONGODB_URI && \
echo '📦 Redis Host: '$SPRING_DATA_REDIS_HOST && \
java $JAVA_OPTS \
-Dspring.profiles.active=production \
-Dspring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:-mongodb://admin:totvs123456789@totvs-mongodb:27017/totvs_integration?authSource=admin} \
-Dspring.data.mongodb.database=${SPRING_DATA_MONGODB_DATABASE:-totvs_integration} \
-Dspring.data.redis.host=${SPRING_DATA_REDIS_HOST:-totvs-redis} \
-Dspring.data.redis.port=${SPRING_DATA_REDIS_PORT:-6379} \
-Dspring.data.redis.password=${SPRING_DATA_REDIS_PASSWORD:-totvs123456789} \
-Dspring.cache.type=redis \
-Dapp.description='Hub de integração para sistemas TOTVS' \
-Dserver.port=8080 \
-Dmanagement.endpoints.web.exposure.include=health,info,metrics \
-Dmanagement.endpoint.health.show-details=always \
-jar app.jar"]