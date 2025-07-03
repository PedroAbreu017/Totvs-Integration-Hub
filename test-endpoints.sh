#!/bin/bash

# Script de Teste dos Endpoints - TOTVS Integration Hub
echo "🧪 TESTE DOS ENDPOINTS - TOTVS Integration Hub"
echo "==============================================="

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

BASE_URL="http://localhost:8080/api"

# Função para logs
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

# Função para testar endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    
    log_info "Testando: $description"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -w "\n%{http_code}" -X POST \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$BASE_URL$endpoint")
    fi
    
    # Separar body e status code
    body=$(echo "$response" | head -n -1)
    status_code=$(echo "$response" | tail -n 1)
    
    if [ "$status_code" = "200" ] || [ "$status_code" = "201" ]; then
        log_success "$description - Status: $status_code"
        echo "Response: $body" | head -c 100
        echo -e "\n"
    else
        log_error "$description - Status: $status_code"
        echo "Response: $body"
        echo ""
    fi
}

# Verificar se aplicação está rodando
log_info "Verificando se aplicação está rodando..."
if ! curl -s "$BASE_URL/v1/health" > /dev/null; then
    log_error "Aplicação não está rodando em $BASE_URL"
    echo ""
    echo "Para iniciar a aplicação:"
    echo "  ./start.sh"
    echo ""
    exit 1
fi

log_success "Aplicação está rodando!"
echo ""

# Testes dos endpoints
echo "🔍 TESTANDO ENDPOINTS:"
echo "====================="

# 1. Health Check
test_endpoint "GET" "/v1/health" "Health Check"

# 2. Tipos de Conectores
test_endpoint "GET" "/connectors/types" "Tipos de Conectores"

# 3. Schema PostgreSQL
test_endpoint "GET" "/connectors/DATABASE_POSTGRESQL/schema" "Schema PostgreSQL"

# 4. Templates de Conectores
test_endpoint "GET" "/connectors/templates" "Templates de Conectores"

# 5. Templates Específicos
test_endpoint "GET" "/connectors/templates?type=DATABASE_POSTGRESQL" "Template PostgreSQL Específico"

# 6. Validar Configuração Válida
valid_config='{
  "type": "DATABASE_POSTGRESQL",
  "configuration": {
    "host": "localhost",
    "port": 5432,
    "database": "testdb",
    "username": "user",
    "password": "pass"
  }
}'

test_endpoint "POST" "/connectors/validate" "Validar Configuração Válida" "$valid_config"

# 7. Validar Configuração Inválida
invalid_config='{
  "type": "DATABASE_POSTGRESQL",
  "configuration": {
    "host": "localhost"
  }
}'

test_endpoint "POST" "/connectors/validate" "Validar Configuração Inválida" "$invalid_config"

# 8. Testar Conector
test_config='{
  "type": "DATABASE_POSTGRESQL",
  "configuration": {
    "host": "localhost",
    "port": 5432,
    "database": "testdb",
    "username": "user",
    "password": "pass"
  }
}'

test_endpoint "POST" "/connectors/test" "Testar Conector" "$test_config"

# 9. Sistema - Info
test_endpoint "GET" "/v1/system/info" "Informações do Sistema"

# 10. Sistema - Ping
test_endpoint "GET" "/test/ping" "Ping Test"

# 11. Criar Tenant
tenant_data='{
  "name": "Test Company",
  "domain": "test-company",
  "email": "admin@test.com",
  "plan": "FREE",
  "description": "Tenant de teste"
}'

test_endpoint "POST" "/v1/tenants" "Criar Tenant" "$tenant_data"

echo ""
echo "📊 RESUMO DOS TESTES:"
echo "===================="
echo ""
echo "🌐 URLs Importantes:"
echo "   • API Base: $BASE_URL"
echo "   • Health Check: $BASE_URL/v1/health"
echo "   • Swagger UI: $BASE_URL/swagger-ui.html"
echo "   • Conectores: $BASE_URL/connectors/types"
echo ""
echo "📋 Comandos úteis:"
echo "   # Health check"
echo "   curl $BASE_URL/v1/health"
echo ""
echo "   # Tipos de conectores"
echo "   curl $BASE_URL/connectors/types"
echo ""
echo "   # Swagger (abrir no browser)"
echo "   open $BASE_URL/swagger-ui.html"
echo ""

log_success "Testes concluídos!"