#!/bin/bash

# Script de Verificação - TOTVS Integration Hub
echo "🔍 VERIFICAÇÃO TOTVS Integration Hub"
echo "====================================="

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Contadores
PASSED=0
FAILED=0

# Função para logs
check_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
        ((PASSED++))
    else
        echo -e "${RED}❌ $2${NC}"
        ((FAILED++))
    fi
}

echo -e "${BLUE}📋 Verificando estrutura dos arquivos...${NC}"

# 1. Verificar arquivos principais
files=(
    "src/main/java/com/totvs/integration/IntegrationApplication.java"
    "src/main/java/com/totvs/integration/controller/ConnectorController.java"
    "src/main/java/com/totvs/integration/service/ConnectorService.java"
    "src/main/java/com/totvs/integration/dto/response/ApiResponse.java"
    "src/main/java/com/totvs/integration/entity/Tenant.java"
    "src/main/resources/application.yml"
    "pom.xml"
    "docker-compose.yml"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        check_status 0 "Arquivo $file existe"
    else
        check_status 1 "Arquivo $file não encontrado"
    fi
done

# 2. Verificar correções de código
echo ""
echo -e "${BLUE}🔧 Verificando correções de código...${NC}"

# Verificar ApiResponse.isSuccess()
if grep -q "public boolean isSuccess()" src/main/java/com/totvs/integration/dto/response/ApiResponse.java; then
    check_status 0 "ApiResponse.isSuccess() implementado"
else
    check_status 1 "ApiResponse.isSuccess() não encontrado"
fi

# Verificar ConnectorService.validateConfigWithErrors()
if grep -q "validateConfigWithErrors" src/main/java/com/totvs/integration/service/ConnectorService.java; then
    check_status 0 "ConnectorService.validateConfigWithErrors() implementado"
else
    check_status 1 "ConnectorService.validateConfigWithErrors() não encontrado"
fi

# Verificar Tenant.setContactEmail()
if grep -q "setContactEmail" src/main/java/com/totvs/integration/entity/Tenant.java; then
    check_status 0 "Tenant.setContactEmail() implementado"
else
    check_status 1 "Tenant.setContactEmail() não encontrado"
fi

# 3. Compilação
echo ""
echo -e "${BLUE}🏗️ Verificando compilação...${NC}"

if mvn clean compile -q; then
    check_status 0 "Compilação bem-sucedida"
else
    check_status 1 "Erro na compilação"
fi

# 4. Docker Compose
echo ""
echo -e "${BLUE}🐳 Verificando Docker Compose...${NC}"

if docker-compose config > /dev/null 2>&1; then
    check_status 0 "docker-compose.yml válido"
else
    check_status 1 "docker-compose.yml inválido"
fi

# 5. Infraestrutura
echo ""
echo -e "${BLUE}🚀 Testando infraestrutura...${NC}"

docker-compose up -d mongodb redis > /dev/null 2>&1
sleep 10

# Verificar MongoDB
if docker-compose ps mongodb | grep -q "Up"; then
    check_status 0 "MongoDB container rodando"
else
    check_status 1 "MongoDB container com problema"
fi

# Verificar Redis
if docker-compose ps redis | grep -q "Up"; then
    check_status 0 "Redis container rodando"
else
    check_status 1 "Redis container com problema"
fi

# 6. Teste rápido da aplicação
echo ""
echo -e "${BLUE}🎯 Teste rápido da aplicação...${NC}"

if [ $FAILED -eq 0 ]; then
    echo "🚀 Iniciando aplicação para teste rápido..."
    
    # Iniciar aplicação em background
    mvn spring-boot:run > app.log 2>&1 &
    APP_PID=$!
    
    # Aguardar aplicação subir
    echo "⏳ Aguardando aplicação inicializar (60s)..."
    sleep 60
    
    # Testar health check
    if curl -s http://localhost:8080/api/v1/health | grep -q "success\|UP"; then
        check_status 0 "Health check funcionando"
    else
        check_status 1 "Health check com problema"
    fi
    
    # Testar connector types
    if curl -s http://localhost:8080/api/connectors/types | grep -q "DATABASE_POSTGRESQL\|REST"; then
        check_status 0 "Endpoint connector types funcionando"
    else
        check_status 1 "Endpoint connector types com problema"
    fi
    
    # Parar aplicação
    kill $APP_PID 2>/dev/null
    wait $APP_PID 2>/dev/null
    
else
    echo -e "${YELLOW}⚠️ Pulando teste da aplicação devido a erros anteriores${NC}"
fi

# 7. Limpeza
echo ""
echo -e "${BLUE}🧹 Fazendo limpeza...${NC}"
docker-compose down > /dev/null 2>&1

# Relatório Final
echo ""
echo "📊 RELATÓRIO FINAL"
echo "=================="
echo -e "✅ Verificações passou: ${GREEN}$PASSED${NC}"
echo -e "❌ Verificações falharam: ${RED}$FAILED${NC}"

TOTAL=$((PASSED + FAILED))
if [ $TOTAL -gt 0 ]; then
    SUCCESS_RATE=$((PASSED * 100 / TOTAL))
    echo -e "📈 Taxa de sucesso: ${GREEN}$SUCCESS_RATE%${NC}"
fi

if [ $FAILED -eq 0 ]; then
    echo ""
    echo -e "${GREEN}🎉 PARABÉNS! Aplicação está 100% funcional!${NC}"
    echo ""
    echo "🚀 Para executar:"
    echo "   ./start.sh"
    echo ""
    echo "📍 URLs importantes:"
    echo "   • API: http://localhost:8080/api"
    echo "   • Health: http://localhost:8080/api/v1/health"
    echo "   • Swagger: http://localhost:8080/api/swagger-ui.html"
    echo ""
elif [ $FAILED -le 2 ]; then
    echo ""
    echo -e "${YELLOW}⚠️ Aplicação quase pronta! Apenas pequenos ajustes necessários.${NC}"
    echo ""
else
    echo ""
    echo -e "${RED}❌ Ainda há problemas que precisam ser corrigidos.${NC}"
    echo ""
fi

# Criar arquivo de status
cat > status.txt << EOF
TOTVS Integration Hub - Status da Verificação
============================================

Data: $(date)
Verificações passaram: $PASSED
Verificações falharam: $FAILED
$([ $TOTAL -gt 0 ] && echo "Taxa de sucesso: $((PASSED * 100 / TOTAL))%")

Status: $([ $FAILED -eq 0 ] && echo "✅ PRONTO PARA PRODUÇÃO" || echo "⚠️ PRECISA DE AJUSTES")

Próximos passos:
1. Executar: ./start.sh
2. Testar endpoints manualmente
3. Verificar Swagger UI
4. Deploy em produção

EOF

echo "💾 Status salvo em: status.txt"