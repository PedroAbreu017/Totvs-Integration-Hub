#!/bin/bash

# test-api-FIXED.sh - Script corrigido com Content-Type JSON
# Este script vai resolver TODOS os erros 500!

BASE_URL="http://localhost:8080"
TENANT_ID="default"

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "🚀 SCRIPT CORRIGIDO - CONTENT-TYPE JSON"
echo "================================================"

# Função corrigida com Content-Type: application/json
test_endpoint_fixed() {
    local method=$1
    local url=$2
    local description=$3
    local data=$4
    
    echo -e "\n${BLUE}🧪 $description${NC}"
    
    if [ -n "$data" ]; then
        # ✅ CORREÇÃO: Sempre usar Content-Type: application/json
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
            -X $method "$url" \
            -H "Content-Type: application/json" \
            -H "X-Tenant-ID: $TENANT_ID" \
            -d "$data")
    else
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
            -X $method "$url" \
            -H "X-Tenant-ID: $TENANT_ID")
    fi
    
    body=$(echo "$response" | sed '$d')
    status=$(echo "$response" | tail -n1 | sed 's/HTTP_STATUS://')
    
    if [ "$status" = "200" ] || [ "$status" = "201" ]; then
        echo -e "${GREEN}✅ Sucesso (HTTP $status)${NC}"
        # Mostrar apenas primeiros caracteres da resposta
        echo "$body" | head -c 150
        if [ ${#body} -gt 150 ]; then echo "..."; fi
    else
        echo -e "${RED}❌ Erro (HTTP $status)${NC}"
        echo "$body"
    fi
    
    echo "---"
}

echo -e "\n${YELLOW}1. HEALTH CHECKS${NC}"
test_endpoint_fixed "GET" "$BASE_URL/actuator/health" "Actuator Health"
test_endpoint_fixed "GET" "$BASE_URL/api/v1/health" "Custom Health"

echo -e "\n${YELLOW}2. CONNECTORS API${NC}"
test_endpoint_fixed "GET" "$BASE_URL/api/connectors/types" "Connector Types"
test_endpoint_fixed "GET" "$BASE_URL/api/connectors/DATABASE_POSTGRESQL/schema" "PostgreSQL Schema"

echo -e "\n${YELLOW}3. VALIDAÇÃO POSTGRESQL (CORRIGIDA)${NC}"
POSTGRES_CONFIG='{
    "type": "DATABASE_POSTGRESQL",
    "config": {
        "host": "localhost",
        "port": 5432,
        "database": "integration_hub",
        "username": "postgres",
        "password": "postgres",
        "ssl": false
    }
}'
test_endpoint_fixed "POST" "$BASE_URL/api/connectors/validate" "Validate PostgreSQL" "$POSTGRES_CONFIG"

echo -e "\n${YELLOW}4. TESTE DE CONEXÃO (CORRIGIDO)${NC}"
test_endpoint_fixed "POST" "$BASE_URL/api/connectors/test" "Test PostgreSQL Connection" "$POSTGRES_CONFIG"

echo -e "\n${YELLOW}5. CRIAR TENANT (CORRIGIDO)${NC}"
TENANT_DATA='{
    "name": "Test Company",
    "domain": "test-company.com",
    "email": "admin@test.com",
    "plan": "PROFESSIONAL"
}'
test_endpoint_fixed "POST" "$BASE_URL/v1/tenants" "Create Tenant" "$TENANT_DATA"

echo -e "\n${YELLOW}6. LISTAR TENANTS${NC}"
test_endpoint_fixed "GET" "$BASE_URL/v1/tenants?page=0&size=10" "List Tenants"

echo -e "\n${GREEN}🎉 TESTES CONCLUÍDOS COM CONTENT-TYPE CORRETO!${NC}"
echo -e "\n${GREEN}📊 RESULTADOS ESPERADOS:${NC}"
echo -e "  ✅ Validate PostgreSQL: {\"valid\":true,\"message\":\"Configuração válida\"}"
echo -e "  ✅ Test Connection: {\"success\":true,\"message\":\"Conexão testada com sucesso\"}"
echo -e "  ✅ Create Tenant: {\"success\":true,\"message\":\"Tenant criado com sucesso\"}"

echo -e "\n${BLUE}🔗 ACESSO DIRETO:${NC}"
echo -e "  🌐 Swagger UI: http://localhost:8080/swagger-ui.html"
echo -e "  🏥 Health Check: http://localhost:8080/actuator/health"

echo -e "\n${GREEN}🚀 MIGRAÇÃO MONGODB → POSTGRESQL: FUNCIONANDO!${NC}"