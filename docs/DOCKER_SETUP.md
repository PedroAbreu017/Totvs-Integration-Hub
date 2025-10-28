# 🐳 Docker Setup Guide - TOTVS Integration Hub

Complete guide for setting up and running TOTVS Integration Hub with Docker.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Service Details](#service-details)
- [Configuration](#configuration)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)
- [Production Deployment](#production-deployment)

---

## Prerequisites

### Required

- **Docker** 20.10+ — [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose** 2.0+ — [Install Docker Compose](https://docs.docker.com/compose/install/)
- **Git** — [Install Git](https://git-scm.com/)

### Optional (for local development)

- **Java 17+** — [Install Java](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** — [Install Maven](https://maven.apache.org/download.cgi)
- **curl** — Testing API endpoints

### Check Installations

```bash
# Verify Docker
docker --version
docker ps

# Verify Docker Compose
docker-compose --version

# Verify Git
git --version
```

---

## Quick Start

### 3-Step Setup

```bash
# 1. Clone repository
git clone https://github.com/yourusername/totvs-integration-hub.git
cd totvs-integration-hub

# 2. Make scripts executable
chmod +x scripts/*.sh

# 3. Start all services
./scripts/start.sh
```

That's it! ✨

### Verify Services

```bash
# Run verification script
./scripts/verify.sh

# Or manually check
curl http://localhost:8080/actuator/health
```

---

## Project Structure

```
totvs-integration-hub/
├── 📁 docker/
│   ├── Dockerfile                 # Application container definition
│   └── docker-compose.yml         # Multi-service orchestration
│
├── 📁 scripts/
│   ├── start.sh                   # Start all services
│   ├── stop.sh                    # Stop all services
│   ├── verify.sh                  # Verify services health
│   └── test-api.sh                # Run API tests
│
├── 📁 init-scripts/
│   └── init-database.sql          # Initial database setup (optional)
│
├── .env.example                   # Environment variables template
├── docker-compose.yml             # Main docker-compose file
├── Dockerfile                     # Application container
└── README.md                      # Main documentation
```

---

## Service Details

### PostgreSQL 15

**Container:** `integration-postgres`

```yaml
Image: postgres:15-alpine
Port: 5433:5432
Database: integration_hub
User: postgres
Password: postgres
```

**Features:**
- Alpine Linux base (lightweight)
- Health checks built-in
- Persistent volume: `postgres_data`
- Init scripts support
- Max connections: 200

**Connect:**

```bash
# Using psql
psql -h localhost -p 5433 -U postgres -d integration_hub

# Using Docker
docker exec -it integration-postgres psql -U postgres

# Connection string
jdbc:postgresql://localhost:5433/integration_hub
```

### Redis 7

**Container:** `integration-redis`

```yaml
Image: redis:7-alpine
Port: 6379:6379
```

**Features:**
- Alpine Linux base
- AOF persistence enabled
- Max memory: 512MB
- LRU eviction policy
- Health checks

**Connect:**

```bash
# Using redis-cli
redis-cli -h localhost -p 6379

# Using Docker
docker exec -it integration-redis redis-cli

# Test connection
redis-cli ping  # Should return "PONG"
```

### PgAdmin 4

**Container:** `integration-pgadmin`

```yaml
Image: dpage/pgadmin4:latest
Port: 8081:80
Email: admin@totvs.com
Password: admin123
```

**Access:** http://localhost:8081

**Features:**
- Web-based PostgreSQL management
- No server mode (single user)
- Automatic server connection to PostgreSQL

### Application

**Container:** `integration-app`

```yaml
Image: totvs-integration-hub:latest
Port: 8080:8080
```

**Environment:**
- Spring profiles: dev, prod
- Log level: INFO
- Database: PostgreSQL (via JDBC)
- Cache: Redis
- Multi-tenancy: Enabled

**Health:** http://localhost:8080/actuator/health

---

## Configuration

### Environment Variables

Create `.env` from `.env.example`:

```bash
cp .env.example .env
```

**Key variables:**

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5433/integration_hub
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Application
APP_PORT=8080
APP_ENVIRONMENT=development

# Multi-tenancy
MULTI_TENANT_ENABLED=true
MULTI_TENANT_HEADER=X-Tenant-ID
```

### Docker Networks

Services communicate via `integration-network`:

```yaml
networks:
  integration-network:
    driver: bridge
    subnet: 172.28.0.0/16
```

**DNS Resolution:** Use container names as hostnames:
- `postgres:5432` (internal)
- `redis:6379` (internal)
- `app:8080` (internal)

---

## Usage

### Start Services

```bash
# Using provided script (recommended)
./scripts/start.sh

# Or manually with docker-compose
docker-compose up -d
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
docker-compose logs -f redis
docker-compose logs -f app

# Last N lines
docker-compose logs --tail=50 app
```

### Stop Services

```bash
# Using script
./scripts/stop.sh

# Or manually
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop and remove everything (including volumes)
docker-compose down -v
```

### Check Service Status

```bash
# View running containers
docker-compose ps

# View all containers (including stopped)
docker ps -a

# View container details
docker inspect integration-postgres
```

### Access Services

**PostgreSQL:**

```bash
# Via psql
psql -h localhost -p 5433 -U postgres

# Via Docker exec
docker exec -it integration-postgres psql -U postgres -d integration_hub

# List tables
\dt

# Exit
\q
```

**Redis:**

```bash
# Via redis-cli
redis-cli -h localhost

# Via Docker exec
docker exec -it integration-redis redis-cli

# Ping
> ping

# Exit
> exit
```

**Application:**

```bash
# API Health
curl http://localhost:8080/actuator/health

# Swagger UI
open http://localhost:8080/swagger-ui.html

# Create tenant (example)
curl -X POST http://localhost:8080/v1/tenants \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Tenant"}'
```

**PgAdmin:**

```
URL: http://localhost:8081
Email: admin@totvs.com
Password: admin123
```

### Database Initialization

To run custom init scripts:

1. Create SQL script in `init-scripts/` directory
2. Scripts run automatically on first PostgreSQL start
3. Example: `init-scripts/01-init-database.sql`

```sql
-- init-scripts/01-init-database.sql
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS public;

-- Your custom setup here
```

### Rebuild Image

```bash
# Rebuild application image
docker-compose build app

# Rebuild all images
docker-compose build

# Rebuild and restart
docker-compose up -d --build
```

---

## Troubleshooting

### Issue: Port Already in Use

```bash
# Check what's using the port
lsof -i :5433  # PostgreSQL
lsof -i :6379  # Redis
lsof -i :8081  # PgAdmin
lsof -i :8080  # Application

# Or change ports in docker-compose.yml
# postgres: 5433:5432 → 5434:5432
```

### Issue: Container Failed to Start

```bash
# Check logs
docker-compose logs postgres
docker-compose logs redis

# Restart service
docker-compose restart postgres

# Full reset
docker-compose down -v
docker-compose up -d
```

### Issue: Connection Refused

```bash
# Verify containers are running
docker ps

# Check network connectivity
docker-compose exec app ping postgres
docker-compose exec app redis-cli -h redis ping

# View network
docker network inspect integration_integration-network
```

### Issue: Database Connection Error

```bash
# Test PostgreSQL connection
docker exec integration-postgres pg_isready -U postgres

# Check PostgreSQL logs
docker-compose logs postgres

# Verify credentials
# Default: user=postgres, password=postgres
```

### Issue: Redis Connection Timeout

```bash
# Test Redis connection
docker exec integration-redis redis-cli ping

# Check Redis logs
docker-compose logs redis

# Restart Redis
docker-compose restart redis
```

### Issue: Out of Disk Space

```bash
# Check Docker disk usage
docker system df

# Clean up unused resources
docker system prune -a --volumes

# Remove specific volume
docker volume rm integration_postgres_data
```

### Clean Everything (Nuclear Option)

```bash
# Stop all services
docker-compose down

# Remove all containers
docker-compose rm -f

# Remove all volumes (WARNING: deletes data)
docker volume prune -f

# Remove unused images
docker image prune -a

# Fresh start
docker-compose up -d
```

---

## Production Deployment

### Pre-Production Checklist

- [ ] Update `.env` with production credentials
- [ ] Use external PostgreSQL/Redis (managed services)
- [ ] Configure SSL/TLS certificates
- [ ] Set up reverse proxy (Nginx, HAProxy)
- [ ] Configure monitoring and logging
- [ ] Test database backups
- [ ] Review security settings

### Environment Variables (Production)

```bash
# Update .env
DB_URL=jdbc:postgresql://prod-db.cloud:5432/integration_hub
DB_USERNAME=prod_user
DB_PASSWORD=<secure-password>
REDIS_HOST=prod-redis.cloud
REDIS_PORT=6379
REDIS_PASSWORD=<secure-password>
APP_ENVIRONMENT=production
APP_LOG_LEVEL=WARN
SPRING_PROFILES_ACTIVE=prod
```

### Docker Compose (Production)

```bash
# Start services
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Scale replicas
docker-compose up -d --scale app=3
```

### Kubernetes Deployment

```bash
# Build and push image
docker build -t yourreg.azurecr.io/totvs-integration-hub:latest .
docker push yourreg.azurecr.io/totvs-integration-hub:latest

# Deploy to Kubernetes
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
```

### Monitoring

```bash
# View resource usage
docker stats

# Monitor application logs
docker-compose logs -f app

# Set up log aggregation (ELK, Splunk, etc.)
# Configure in docker-compose.yml logging drivers
```

---

## Performance Tuning

### Database Connection Pool

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # Max connections
      minimum-idle: 5             # Min idle connections
      connection-timeout: 30000   # 30 seconds
```

### Redis Memory

```bash
# Check memory usage
docker exec integration-redis redis-cli info memory

# Adjust in docker-compose.yml
command: redis-server --maxmemory 1gb --maxmemory-policy allkeys-lru
```

### JVM Tuning

```dockerfile
# In Dockerfile
ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC"
```

---

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [Redis Docker Hub](https://hub.docker.com/_/redis)
- [Spring Boot Docker Support](https://spring.io/guides/gs/spring-boot-docker/)

---

**Last Updated:** January 2024  
**Maintainer:** TOTVS Engineering Team