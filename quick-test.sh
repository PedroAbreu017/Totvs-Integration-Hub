#!/bin/bash

# Teste rápido para verificar se aplicação está funcionando
echo "🧪 TESTE RÁPIDO - TOTVS Integration Hub"
echo "======================================="

BASE_URL="http://localhost:8080/api"

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

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Função para testar endpoint
test_endpoint() {
    local url=$1
    local desc=$2
    
    echo -n "Testing $desc... "
    
    if curl -s --max-time 5 "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✅${NC}"
        return 0
    else
        echo -e "${RED}❌${NC}"
        return 1
    fi
}

# Verificar se aplicação está rodando
log_info "Verificando se aplicação está respondendo..."

# Testes básicos
test_endpoint "$BASE_URL/v1/health" "Health Check"
test_endpoint "$BASE_URL/connectors/types" "Connector Types"
test_endpoint "$BASE_URL/test/ping" "Ping Test"

echo ""
echo "📊 TESTE COMPLETO:"

# Health check detalhado
echo "🔍 Health Check Response:"
curl -s "$BASE_URL/v1/health" | head -c 200
echo ""

# Connector types
echo "🔌 Connector Types:"
curl -s "$BASE_URL/connectors/types" | head -c 200
echo ""

# Test ping
echo "🏓 Ping Response:"
curl -s "$BASE_URL/test/ping" | head -c 200
echo ""

echo ""
echo "🌐 URLs para testar no browser:"
echo "   • Swagger UI: http://localhost:8080/api/swagger-ui.html"
echo "   • Health: http://localhost:8080/api/v1/health"
echo "   • Conectores: http://localhost:8080/api/connectors/types"