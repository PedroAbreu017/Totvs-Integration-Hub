#!/bin/bash

# Script de inicialização simples - sem complexidades
echo "🚀 INICIALIZAÇÃO SIMPLES - TOTVS Integration Hub"
echo "================================================"

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# 1. Parar processos existentes
log_info "Parando processos existentes..."
pkill -f "spring-boot:run" 2>/dev/null || true

# 2. Verificar porta 8080
log_info "Verificando porta 8080..."
if lsof -i :8080 &>/dev/null; then
    log_warning "Porta 8080 ocupada. Liberando..."
    lsof -ti :8080 | xargs kill -9 2>/dev/null || true
    sleep 2
fi

# 3. Verificar containers básicos
log_info "Verificando containers..."
docker-compose up -d mongodb redis &>/dev/null

# Aguardar containers
sleep 10

# 4. Configurações mínimas
export SPRING_PROFILES_ACTIVE=dev
export JAVA_OPTS="-Xmx512m -Xms256m"
export LOGGING_LEVEL_ROOT=WARN
export LOGGING_LEVEL_COM_TOTVS_INTEGRATION=INFO

# 5. Compilar se necessário
if [ ! -d "target/classes" ]; then
    log_info "Compilando aplicação..."
    mvn clean compile -q
fi

log_success "Configuração completa!"
echo ""
echo "🎯 Iniciando aplicação..."
echo "📍 URLs para testar após inicializar:"
echo "   • Health: http://localhost:8080/api/v1/health"
echo "   • Conectores: http://localhost:8080/api/connectors/types"
echo "   • Swagger: http://localhost:8080/api/swagger-ui.html"
echo ""
echo "⏳ Aguarde a mensagem: 'Started IntegrationApplication'"
echo "🛑 Para parar: Ctrl+C"
echo ""

# 6. Executar aplicação
mvn spring-boot:run