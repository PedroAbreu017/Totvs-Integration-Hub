# 🚀 TOTVS Integration Hub

> **Sistema de Integração Empresarial Multi-tenant com PostgreSQL + Redis**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7.4+-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)

## 📋 Sobre o Projeto

O **TOTVS Integration Hub** é uma plataforma robusta de integração empresarial que permite conectar diferentes sistemas, bancos de dados e APIs de forma segura e escalável. Desenvolvido com arquitetura multi-tenant, oferece suporte a múltiplos conectores e execução de integrações em tempo real.

### 🎯 Principais Funcionalidades

- 🔌 **11 Tipos de Conectores**: PostgreSQL, MySQL, Oracle, SQL Server, REST API, Email, Arquivos (CSV/JSON/XML), MongoDB, Webhooks
- 🏢 **Multi-tenancy**: Isolamento completo de dados por tenant
- 📊 **Monitoramento**: Health checks, métricas e logs de execução
- 🔒 **Segurança**: Rate limiting, validação de dados e interceptors
- 📈 **Performance**: Redis para cache, pool de conexões otimizado
- 🧪 **Qualidade**: 47+ testes automatizados
- 📚 **Documentação**: Swagger UI integrado

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
│   ├── start.sh                       # Iniciar aplicação
│   ├── deploy.sh                      # Deploy automatizado
│   ├── test-api.sh                    # Testes da API
│   └── verify.sh                      # Verificação do sistema
└── 📖 README.md                       # Este arquivo
```

## 🚀 Como Executar

### Pré-requisitos

- ☕ **Java 17+**
- 🔨 **Maven 3.8+**
- 🐳 **Docker & Docker Compose**
- 🌐 **Git**

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
```

### 3. Executar a Aplicação

```bash
# Opção 1: Usando Maven
mvn spring-boot:run

# Opção 2: Usando script
./start.sh

# Opção 3: Compilar e executar JAR
mvn clean package
java -jar target/integration-prototype-1.0.0-SNAPSHOT.jar
```

### 4. Verificar se Está Funcionando

```bash
# Executar testes da API
./test-api.sh

# Ou testar manualmente
curl http://localhost:8080/actuator/health
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
```

## 🔧 Configuração

### Banco de Dados

**PostgreSQL (Produção)**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/integration_hub
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
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
      port: 6379
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
```

### Testes da API (Funcionais)

```bash
# Script completo de testes
./test-api.sh

# Testes específicos
./test-endpoints.sh
./verify.sh
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
  postgres:    # PostgreSQL 15
  redis:       # Redis 7.4
  pgadmin:     # PgAdmin 4 (Web UI)
```

### Acessar Serviços

- **PostgreSQL**: `localhost:5432`
- **Redis**: `localhost:6379`
- **PgAdmin**: `http://localhost:8081`
  - Email: `admin@totvs.com`
  - Senha: `admin123`

## 📊 Monitoramento

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
http://localhost:8080/actuator/prometheus
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
./deploy.sh dev

# Manual
mvn clean package
docker-compose up -d
java -jar target/*.jar
```

### Produção

```bash
# Deploy automatizado
./deploy.sh prod

# Variáveis de ambiente necessárias:
export DB_URL=jdbc:postgresql://prod-db:5432/integration_hub
export DB_USERNAME=prod_user
export DB_PASSWORD=secure_password
export REDIS_HOST=prod-redis
```

## 📈 Performance

### Benchmarks Observados

- **Startup Time**: ~10 segundos
- **Health Check**: < 1ms
- **Database Connection**: < 100ms
- **Redis Connection**: < 50ms
- **API Response**: 1-5ms (média)

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
```

**2. Erro de Serialização JSON**
```bash
# Verificar logs da aplicação
tail -f logs/application.log

# Testar endpoint específico
curl -X POST -H "Content-Type: application/json" ...
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

## 📝 Licença

Este projeto é propriedade da **TOTVS** e está licenciado sob termos proprietários.

## 📞 Suporte

- **Documentação**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Logs**: `logs/application.log`

---

**✅ Migração MongoDB → PostgreSQL concluída com sucesso!**  
*Desenvolvido com ❤️ pela equipe TOTVS*