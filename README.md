# 🚀 TOTVS Integration Hub

> **Sistema de Integração Empresarial Multi-tenant com PostgreSQL + Redis**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7.4+-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Tests-47%2B-brightgreen.svg)](src/test)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE)

## 📋 Sobre o Projeto

O **TOTVS Integration Hub** é uma plataforma robusta de integração empresarial que permite conectar diferentes sistemas, bancos de dados e APIs de forma segura e escalável. Desenvolvido com arquitetura multi-tenant, oferece suporte a múltiplos conectores e execução de integrações em tempo real.

### 🚀 Quick Start

```bash
# 1. Clone
git clone <repository-url>
cd totvs-integration-prototype

# 2. Inicie os serviços
docker-compose up -d

# 3. Pronto! Acesse:
curl http://localhost:8081/actuator/health
open http://localhost:8081/swagger-ui.html
```

**Tudo rodando em 2 minutos!** ✨

### 🎯 Principais Funcionalidades

- 🔌 **11 Tipos de Conectores**: PostgreSQL, MySQL, Oracle, SQL Server, REST API, Email, Arquivos (CSV/JSON/XML), MongoDB, Webhooks
- 🏢 **Multi-tenancy**: Isolamento completo de dados por tenant
- 📊 **Monitoramento**: Health checks, métricas e logs de execução
- 🔒 **Segurança**: Rate limiting, validação de dados e interceptors
- 📈 **Performance**: Redis para cache, pool de conexões otimizado
- 🧪 **Qualidade**: 47+ testes automatizados
- 📚 **Documentação**: Swagger UI integrado
- ✅ **Production Ready**: Docker Compose, Health Checks, Prometheus

## 📊 Portas e Acessos

| Serviço | Porta | URL | Credenciais |
|---------|-------|-----|-------------|
| **PostgreSQL** | 5435 | `jdbc:postgresql://localhost:5435/integration_hub` | user: `postgres` / pass: `postgres` |
| **Redis** | 6380 | `redis://localhost:6380` | — |
| **Application** | 8081 | `http://localhost:8081` | — |
| **Swagger UI** | 8081 | `http://localhost:8081/swagger-ui.html` | — |
| **PgAdmin** | 8082 | `http://localhost:8082` | user: `admin@totvs.com` / pass: `admin123` |

## 🏗️ Arquitetura

### Stack Tecnológica

```
Frontend API:     Spring Boot 3.1.5 + Java 17
Database:         PostgreSQL 15+ (Produção) + H2 (Testes)
Cache:            Redis 7.4+
ORM:              Hibernate/JPA
Build:            Maven
Containers:       Docker + Docker Compose
Monitoring:       Actuator + Prometheus
Documentation:    Swagger/OpenAPI 3
```

### Estrutura do Projeto

```
totvs-integration-prototype/
├── 📁 src/main/java/com/totvs/integration/
│   ├── 🔧 config/                     # Configurações (Redis, Swagger, Web)
│   ├── 🔌 connector/                  # Conectores (Database, REST, Email, File)
│   ├── 🎮 controller/                 # Controllers REST API
│   ├── 📦 dto/                        # Data Transfer Objects
│   │   ├── connector/                 # DTOs específicos de conectores
│   │   ├── request/                   # Request DTOs
│   │   └── response/                  # Response DTOs
│   ├── 🗄️ entity/                     # Entidades JPA
│   ├── ⚠️ exception/                  # Tratamento de exceções
│   ├── 📚 repository/                 # Repositórios JPA
│   ├── 🔐 security/                   # Multi-tenancy e segurança
│   └── ⚙️ service/                    # Lógica de negócio
├── 📁 src/main/resources/
│   ├── application.yml                # Configuração principal
│   └── application-production.yml     # Configuração de produção
├── 📁 src/test/                       # Testes automatizados
├── 🐳 docker-compose.yml             # PostgreSQL + Redis + PgAdmin
├── 📊 monitoring/prometheus.yml       # Configuração Prometheus
├── 🔧 Scripts utilitários:
│   ├── scripts/start.sh               # Iniciar aplicação
│   ├── scripts/stop.sh                # Parar serviços
│   ├── scripts/verify.sh              # Verificação do sistema
│   └── scripts/test-api.sh            # Testes da API
├── 📁 docs/                           # Documentação técnica
└── 📖 README.md                       # Este arquivo
```

## 🚀 Como Executar

### Pré-requisitos

- ☕ **Java 17+** — [Download](https://www.oracle.com/java/technologies/downloads/)
- 🔨 **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- 🐳 **Docker & Docker Compose** — [Download](https://www.docker.com/products/docker-desktop)
- 🌐 **Git** — [Download](https://git-scm.com/)

### 1. Clonar o Repositório

```bash
git clone <repository-url>
cd totvs-integration-prototype
```

### 2. Subir a Infraestrutura (PostgreSQL + Redis)

```bash
# Subir containers
docker-compose up -d

# Verificar se está rodando
docker-compose ps

# Esperado: 4 containers com status "Up"
```

### 3. Executar a Aplicação

```bash
# Opção 1: Usando Maven
mvn spring-boot:run

# Opção 2: Usando script
./scripts/start.sh

# Opção 3: Compilar e executar JAR
mvn clean package
java -jar target/integration-prototype-1.0.0-SNAPSHOT.jar
```

### 4. Verificar se Está Funcionando

```bash
# Health check
curl http://localhost:8081/actuator/health

# Resultado esperado:
# {"status":"UP","components":{"db":{"status":"UP"},"redis":{"status":"UP"}...}}

# Acessar interfaces:
open http://localhost:8081/swagger-ui.html  # API Documentation
open http://localhost:8082                   # PgAdmin (Database Management)
```

## 🧪 Quick Test

```bash
# 1. Health check
curl http://localhost:8081/actuator/health

# 2. List tenants
curl http://localhost:8081/v1/tenants

# 3. System info
curl http://localhost:8081/v1/system/info

# 4. Access UI
open http://localhost:8081/swagger-ui.html
```

## 🌐 Endpoints da API

### 🏥 Health & Monitoring

```http
GET /actuator/health              # Spring Boot Actuator health
GET /api/v1/health               # Health customizado com detalhes
GET /api/v1/health/simple        # Health simples (OK/FAIL)
GET /api/v1/health/ready         # Readiness probe
GET /api/v1/health/live          # Liveness probe
GET /v1/system/info              # Informações do sistema
```

### 🔌 Connectors API

```http
GET    /api/connectors/types                    # Listar tipos disponíveis
GET    /api/connectors/{type}/schema           # Schema de configuração
POST   /api/connectors/validate               # Validar configuração
POST   /api/connectors/test                   # Testar conectividade
GET    /api/connectors/templates              # Templates de configuração
```

### 🏢 Tenants Management

```http
GET    /v1/tenants                            # Listar tenants (paginado)
POST   /v1/tenants                           # Criar novo tenant
GET    /v1/tenants/{id}                      # Obter tenant específico
PUT    /v1/tenants/{id}                      # Atualizar tenant
DELETE /v1/tenants/{id}                      # Deletar tenant
GET    /v1/tenants/{id}/stats                # Estatísticas do tenant
POST   /v1/tenants/{id}/regenerate-api-key   # Regenerar API key
```

### 🔗 Integrations Management

```http
GET    /v1/integrations                      # Listar integrações
POST   /v1/integrations                     # Criar integração
GET    /v1/integrations/{id}                # Obter integração
PUT    /v1/integrations/{id}                # Atualizar integração
DELETE /v1/integrations/{id}                # Deletar integração
POST   /v1/integrations/{id}/execute        # Executar integração
GET    /v1/integrations/{id}/logs           # Logs de execução
```

### 📚 Documentação

```http
GET /swagger-ui.html                         # Interface Swagger UI
GET /v3/api-docs                            # Especificação OpenAPI 3
GET /actuator/prometheus                    # Prometheus Metrics
```

## 🔧 Configuração

### Banco de Dados

**PostgreSQL (Produção)**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5435/integration_hub
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

**H2 (Testes)**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6380
      timeout: 2000ms
```

### Multi-tenancy

```yaml
app:
  multi-tenant:
    enabled: true
    default-tenant: default
    header-name: X-Tenant-ID
```

## 🧪 Testes

### Executar Todos os Testes

```bash
# Testes unitários e de integração
mvn test

# Testes específicos
mvn test -Dtest=HealthControllerTest
mvn test -Dtest=TenantServiceTest

# Com cobertura
mvn clean test jacoco:report
```

### Testes da API (Funcionais)

```bash
# Script completo de testes
./scripts/test-api.sh

# Verificação de saúde de todos os serviços
./scripts/verify.sh
```

### Cobertura dos Testes

- ✅ **Controllers**: Health, Tenant, Integration, Connector
- ✅ **Services**: TenantService, ConnectorService
- ✅ **Repositories**: TenantRepository
- ✅ **Total**: 47+ testes automatizados

## 🔌 Conectores Suportados

| Tipo | Descrição | Status |
|------|-----------|---------|
| `DATABASE_POSTGRESQL` | PostgreSQL Database | ✅ |
| `DATABASE_MYSQL` | MySQL Database | ✅ |
| `DATABASE_ORACLE` | Oracle Database | ✅ |
| `DATABASE_SQLSERVER` | SQL Server Database | ✅ |
| `REST_API` | REST API endpoints | ✅ |
| `EMAIL_SMTP` | Email SMTP | ✅ |
| `FILE_CSV` | Arquivos CSV | ✅ |
| `FILE_JSON` | Arquivos JSON | ✅ |
| `FILE_XML` | Arquivos XML | ✅ |
| `MONGODB` | MongoDB Database | ✅ |
| `WEBHOOK` | Webhooks HTTP | ✅ |

## 🐳 Docker

### Serviços Disponíveis

```yaml
# docker-compose.yml
services:
  postgres:    # PostgreSQL 15 (porta 5435)
  redis:       # Redis 7.4 (porta 6380)
  pgadmin:     # PgAdmin 4 - Web UI (porta 8082)
  app:         # Application (porta 8081)
```

### Gerenciar Serviços

```bash
# Iniciar todos os serviços
docker-compose up -d

# Parar serviços
docker-compose stop

# Remover containers
docker-compose down

# Ver logs
docker-compose logs -f postgres
docker-compose logs -f redis
docker-compose logs -f app

# Verificar status
docker-compose ps
```

### Acessar Serviços

- **PostgreSQL**: `localhost:5435` (user: `postgres` / pass: `postgres`)
- **Redis**: `localhost:6380`
- **PgAdmin**: `http://localhost:8082` (user: `admin@totvs.com` / pass: `admin123`)
- **Application**: `http://localhost:8081`

## 📊 Monitoramento

### Health Checks

```bash
# Verificação de saúde
curl http://localhost:8081/actuator/health

# Resultado esperado:
{
  "status": "UP",
  "components": {
    "db": { "status": "UP", "details": { "database": "PostgreSQL" } },
    "redis": { "status": "UP", "details": { "version": "7.4" } },
    "diskSpace": { "status": "UP" }
  }
}
```

### Métricas Disponíveis

- **Health Status**: Database, Redis, Disk Space
- **Performance**: Response times, connection pools
- **Business**: Tenants ativos, integrações executadas
- **System**: Memory usage, CPU, uptime

### Prometheus (Opcional)

```bash
# Configuração disponível em:
monitoring/prometheus.yml

# Métricas expostas em:
http://localhost:8081/actuator/prometheus
```

## 🔐 Segurança

### Multi-tenancy

Todas as requisições devem incluir o header:
```http
X-Tenant-ID: {tenant-id}
```

### Rate Limiting

- **Por Tenant**: Configurável via banco de dados
- **Global**: Proteção contra DDoS
- **Por Endpoint**: Limits específicos

### Validação

- **Input Validation**: Todos os DTOs validados
- **SQL Injection**: Prevenção via JPA/Hibernate
- **XSS Protection**: Headers de segurança

## 🚀 Deploy

### Desenvolvimento

```bash
# Usando script automatizado
./scripts/start.sh

# Manual
mvn clean package
docker-compose up -d
java -jar target/*.jar
```

### Produção

```bash
# Deploy automatizado
./scripts/deploy.sh prod

# Variáveis de ambiente necessárias:
export DB_URL=jdbc:postgresql://prod-db:5432/integration_hub
export DB_USERNAME=prod_user
export DB_PASSWORD=secure_password
export REDIS_HOST=prod-redis
```

## 📈 Performance

### Benchmarks Observados

| Métrica | Valor |
|---------|-------|
| **Startup Time** | ~10 segundos |
| **Health Check** | < 1ms |
| **Database Connection** | < 100ms |
| **Redis Connection** | < 50ms |
| **API Response** | 1-5ms (média) |

### Otimizações

- **HikariCP**: Pool de conexões otimizado
- **Redis Caching**: Cache de sessões e dados frequentes
- **JPA Queries**: Queries otimizadas com índices
- **JSON Serialization**: Jackson otimizado

## 🛠️ Troubleshooting

### Problemas Comuns

**1. Erro de Conexão PostgreSQL**
```bash
# Verificar se container está rodando
docker-compose ps

# Ver logs
docker-compose logs postgres

# Reiniciar
docker-compose restart postgres
```

**2. Porta Já em Uso**
```bash
# Verificar qual processo usa a porta
lsof -i :5435  # PostgreSQL
lsof -i :6380  # Redis
lsof -i :8081  # Application
```

**3. Testes Falhando**
```bash
# Limpar e recompilar
mvn clean compile

# Executar com debug
mvn test -X
```

## 🤝 Contribuindo

### Setup para Desenvolvimento

```bash
# 1. Fork e clone o repositório
git clone <your-fork>

# 2. Subir ambiente de desenvolvimento
docker-compose up -d

# 3. Executar testes
mvn test

# 4. Executar aplicação
mvn spring-boot:run
```

### Padrões de Código

- **Java**: Seguir convenções Spring Boot
- **DTOs**: Usar Lombok para reduzir boilerplate
- **Tests**: JUnit 5 + Mockito
- **Documentation**: Javadoc para métodos públicos
- **Commits**: Usar Conventional Commits

## 📚 Documentação Técnica

- [Docker Setup Guide](docs/DOCKER_SETUP.md)
- [Architecture Documentation](docs/ARCHITECTURE.md)
- [API Usage Examples](docs/API_USAGE.md)
- [Deployment Guide](docs/DEPLOYMENT.md)
- [Troubleshooting Guide](docs/TROUBLESHOOTING.md)

## 📝 Licença

Este projeto é propriedade da **TOTVS** e está licenciado sob termos proprietários.

## 📞 Suporte

- **API Documentation**: `http://localhost:8081/swagger-ui.html`
- **Health Check**: `http://localhost:8081/actuator/health`
- **Logs**: `logs/application.log`
- **Database UI**: `http://localhost:8082` (PgAdmin)

---

## ✨ O Que Está Funcionando

- ✅ **47+ Automated Tests** — Todos passando
- ✅ **Multi-Tenant Architecture** — Isolamento completo de dados
- ✅ **PostgreSQL 15 + Redis 7.4** — Production-ready
- ✅ **Docker Compose** — One-command deployment
- ✅ **Health Checks** — Todos os serviços monitorados
- ✅ **API Documentation** — Swagger UI fully integrated
- ✅ **11 Connector Types** — Database, REST, Email, Files, MongoDB, Webhooks
- ✅ **Security** — Rate limiting, input validation, SQL injection prevention

---

**✅ Migração MongoDB → PostgreSQL concluída com sucesso!**  
*Desenvolvido com ❤️ pela equipe TOTVS*  
*Última atualização: Outubro 2024*