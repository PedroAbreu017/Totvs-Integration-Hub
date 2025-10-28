# ✅ Docker Implementation Checklist - Fase 1 Dia 3-4

## Arquivos Criados ✓

Todos esses arquivos já foram criados e estão prontos:

- ✅ `docker-compose.yml` — Completo com PostgreSQL, Redis, PgAdmin, App
- ✅ `.env.example` — Template de variáveis de ambiente
- ✅ `scripts/start.sh` — Script inteligente de startup
- ✅ `scripts/stop.sh` — Script de parada
- ✅ `scripts/verify.sh` — Script de verificação de health checks
- ✅ `Dockerfile` — Multi-stage build otimizado
- ✅ `docs/DOCKER_SETUP.md` — Documentação completa

---

## 🎯 O Que Você Precisa Fazer Agora

### Passo 1: Copiar Arquivos para Seu Projeto

```bash
# No diretório raiz do seu projeto (totvs-integration-prototype):

# Copiar docker-compose.yml
cp /caminho/para/docker-compose.yml ./docker-compose.yml

# Copiar Dockerfile
cp /caminho/para/Dockerfile ./Dockerfile

# Copiar .env.example
cp /caminho/para/.env.example ./.env.example

# Criar diretório scripts (se não existir)
mkdir -p scripts

# Copiar scripts
cp /caminho/para/start.sh ./scripts/start.sh
cp /caminho/para/stop.sh ./scripts/stop.sh
cp /caminho/para/verify.sh ./scripts/verify.sh

# Tornar scripts executáveis
chmod +x scripts/*.sh

# Copiar documentação
mkdir -p docs
cp /caminho/para/DOCKER_SETUP.md ./docs/DOCKER_SETUP.md
```

---

### Passo 2: Criar .env Baseado em .env.example

```bash
# Copiar .env.example para .env
cp .env.example .env

# Editar .env com suas credenciais (se precisar de valores diferentes)
nano .env  # ou use seu editor favorito
```

---

### Passo 3: Testar Localmente (IMPORTANTE!)

```bash
# 1. Para qualquer container antigo
docker-compose down -v

# 2. Inicie os serviços
docker-compose up -d

# 3. Aguarde ~30 segundos
sleep 30

# 4. Verifique se está tudo rodando
docker-compose ps

# Resultado esperado: 3 containers (postgres, redis, pgadmin)
# TODOS com status "Up" (não "Exited")
```

---

### Passo 4: Validar Conectividade

```bash
# Verificar PostgreSQL
docker exec integration-postgres pg_isready -U postgres
# Esperado: accepting connections

# Verificar Redis
docker exec integration-redis redis-cli ping
# Esperado: PONG

# Verificar PgAdmin está acessível
curl -s http://localhost:8081 | head -20
# Esperado: não deve estar vazio
```

---

### Passo 5: Criar Arquivo de Inicialização (Opcional mas Recomendado)

Se o seu projeto precisa de dados iniciais, crie:

```sql
-- init-scripts/01-init.sql
-- Este arquivo roda automaticamente quando PostgreSQL inicia pela primeira vez

CREATE SCHEMA IF NOT EXISTS public;
GRANT ALL ON SCHEMA public TO postgres;

-- Suas tabelas customizadas aqui
-- (se o Hibernate não criar automaticamente)
```

```bash
mkdir -p init-scripts
cat > init-scripts/01-init.sql << 'EOF'
-- Scripts iniciais aqui
EOF
```

---

### Passo 6: Atualizar application.yml (SE NECESSÁRIO)

Se você mudou as portas ou credentials, atualize seu `application.yml`:

```yaml
spring:
  datasource:
    # Verificar se está correto (deve estar se copiar docker-compose.yml tal qual)
    url: jdbc:postgresql://localhost:5433/integration_hub
    username: postgres
    password: postgres
  
  data:
    redis:
      host: localhost
      port: 6379
```

---

### Passo 7: Testar Build da Aplicação

```bash
# Compilar a aplicação
mvn clean package -DskipTests

# Deve terminar com:
# [INFO] BUILD SUCCESS
```

---

### Passo 8: Testar Startup Script

```bash
# Parar containers atuais
docker-compose down

# Usar o script de start (opcional, para testar)
chmod +x scripts/start.sh
# ./scripts/start.sh dev

# Ou manualmente:
docker-compose up -d
mvn spring-boot:run
```

---

## ✅ Checklist de Validação Final

Quando tudo estiver pronto, execute:

```bash
# 1. Verificar containers
docker-compose ps
# ✓ Todos devem estar "Up"

# 2. Executar script de verificação
./scripts/verify.sh
# ✓ Deve retornar "All services are healthy! ✓"

# 3. Testar endpoints da aplicação
curl http://localhost:8080/actuator/health
# ✓ Deve retornar JSON com status "up"

# 4. Acessar Swagger UI
open http://localhost:8080/swagger-ui.html
# ✓ Deve carregar interface Swagger

# 5. Acessar PgAdmin
open http://localhost:8081
# ✓ Deve solicitar login (admin@totvs.com / admin123)

# 6. Testar Database
psql -h localhost -p 5433 -U postgres -d integration_hub -c "\dt"
# ✓ Deve listar tabelas ou estar vazio (se init via Hibernate)
```

---

## 🐛 Se Algo der Errado

### Erro: Port Already in Use

```bash
# Identificar processo usando a porta
lsof -i :5433
lsof -i :6379
lsof -i :8080

# Matar processo ou mudar porta em docker-compose.yml
```

### Erro: Cannot Connect to Docker Daemon

```bash
# Verificar se Docker está rodando
docker ps

# Se não: iniciar Docker Desktop (Windows/Mac) ou Docker daemon (Linux)
# Linux:
sudo systemctl start docker
```

### Erro: PostgreSQL Not Ready

```bash
# Aguardar mais tempo
sleep 60
docker-compose ps

# Se ainda não estiver "Up":
docker-compose logs postgres
```

### Erro: Redis Connection Refused

```bash
# Reiniciar Redis
docker-compose restart redis

# Ou tudo
docker-compose down -v
docker-compose up -d
```

---

## 📊 Estrutura Final Esperada

```
totvs-integration-prototype/
├── docker-compose.yml          ← NOVO
├── Dockerfile                  ← NOVO
├── .env                        ← NOVO (gerado a partir de .env.example)
├── .env.example                ← NOVO
│
├── 📁 scripts/                 ← NOVO
│   ├── start.sh
│   ├── stop.sh
│   ├── verify.sh
│   └── test-api.sh
│
├── 📁 init-scripts/            ← NOVO (opcional)
│   └── 01-init.sql
│
├── 📁 docs/
│   ├── DOCKER_SETUP.md         ← NOVO
│   ├── ARCHITECTURE.md         ← Existente (ou criar)
│   └── ...
│
├── 📁 src/
│   ├── main/
│   └── test/
│
├── pom.xml
├── README.md                   ← ATUALIZAR (já temos versão melhor)
└── ... (outros arquivos)
```

---

## 📝 Próximas Etapas (Depois de Validar)

- [ ] Commit no Git: `git add . && git commit -m "feat: add docker setup"`
- [ ] Push para GitHub: `git push origin main`
- [ ] Atualizar README.md (já temos versão melhor)
- [ ] Executar testes: `./test-api.sh`
- [ ] Passar para **Semana 2 (Testes + Documentação)**

---

## 🎯 Sucesso!

Quando você conseguir:

1. `docker-compose up -d` sem erros
2. `./scripts/verify.sh` retornar "All healthy ✓"
3. Acessar http://localhost:8080/swagger-ui.html
4. Acessar http://localhost:8081 (PgAdmin)

**Parabéns! Fase 1 Dia 3-4 está completa! 🎉**

Aí você avança para **Semana 2: Testes + Documentação**.

---

## 💡 Dicas Importantes

1. **Commits frequentes**: Faça commit depois de cada passo validado
2. **Teste tudo localmente primeiro**: Garanta que funciona antes de fazer push
3. **Não altere versões de containers**: Use exatamente as mesmas
4. **Guarde log de sucesso**: Se funcionou uma vez, há como reproduzir

---

**Status:** Pronto para implementar ✓  
**Tempo estimado:** 30-60 min  
**Complexidade:** Baixa  
**ROI:** Altíssimo (Docker funcionando = portfólio impressionante)

Bora lá! 🚀