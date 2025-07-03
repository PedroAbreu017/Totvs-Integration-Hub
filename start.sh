#!/bin/bash

# TOTVS Integration Hub - Script de Inicialização
echo "🚀 TOTVS Integration Hub - Startup Script"
echo "=========================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para logs coloridos
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

# Verificar pré-requisitos
check_prerequisites() {
    log_info "Verificando pré-requisitos..."
    
    # Verificar Java 17
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge "17" ]; then
            log_success "Java $JAVA_VERSION encontrado"
        else
            log_error "Java 17+ é necessário. Versão atual: $JAVA_VERSION"
            exit 1
        fi
    else
        log_error "Java não encontrado. Instale Java 17+"
        exit 1
    fi
    
    # Verificar Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version 2>&1 | head -n1 | cut -d' ' -f3)
        log_success "Maven $MVN_VERSION encontrado"
    else
        log_error "Maven não encontrado. Instale Maven 3.9+"
        exit 1
    fi
    
    # Verificar Docker
    if command -v docker &> /dev/null; then
        DOCKER_VERSION=$(docker --version | cut -d' ' -f3 | cut -d',' -f1)
        log_success "Docker $DOCKER_VERSION encontrado"
    else
        log_error "Docker não encontrado. Instale Docker"
        exit 1
    fi
    
    # Verificar Docker Compose
    if command -v docker-compose &> /dev/null; then
        COMPOSE_VERSION=$(docker-compose --version | cut -d' ' -f3 | cut -d',' -f1)
        log_success "Docker Compose $COMPOSE_VERSION encontrado"
    else
        log_error "Docker Compose não encontrado"
        exit 1
    fi
}

# Função para setup da infraestrutura
setup_infrastructure() {
    log_info "Configurando infraestrutura..."
    
    # Parar containers existentes
    log_info "Parando containers existentes..."
    docker-compose down 2>/dev/null || true
    
    # Iniciar MongoDB e Redis
    log_info "Iniciando MongoDB e Redis..."
    docker-compose up -d mongodb redis
    
    # Aguardar containers estarem prontos
    log_info "Aguardando containers ficarem prontos..."
    sleep 15
    
    # Verificar MongoDB
    log_info "Verificando MongoDB..."
    for i in {1..30}; do
        if docker-compose exec -T mongodb mongosh --eval "db.adminCommand('ping')" &>/dev/null; then
            log_success "MongoDB está pronto"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "MongoDB não respondeu após 30 tentativas"
            exit 1
        fi
        sleep 2
    done
    
    # Verificar Redis
    log_info "Verificando Redis..."
    for i in {1..30}; do
        if docker-compose exec -T redis redis-cli -a redis123 ping | grep -q PONG; then
            log_success "Redis está pronto"
            break
        fi
        if [ $i -eq 30 ]; then
            log_error "Redis não respondeu após 30 tentativas"
            exit 1
        fi
        sleep 2
    done
}

# Função para build da aplicação
build_application() {
    log_info "Fazendo build da aplicação..."
    
    # Limpar target anterior
    log_info "Limpando build anterior..."
    mvn clean -q
    
    # Compilar aplicação
    log_info "Compilando aplicação..."
    if mvn compile -q; then
        log_success "Compilação concluída"
    else
        log_error "Erro na compilação"
        exit 1
    fi
}

# Função para executar aplicação
run_application() {
    log_info "Iniciando aplicação..."
    
    # Configurar variáveis de ambiente
    export SPRING_PROFILES_ACTIVE=dev
    export JAVA_OPTS="-Xmx512m -Xms256m"
    
    # Executar aplicação
    log_success "🎉 Iniciando TOTVS Integration Hub..."
    echo ""
    echo "📍 URLs importantes:"
    echo "   • Aplicação: http://localhost:8080/api"
    echo "   • Health Check: http://localhost:8080/api/v1/health"
    echo "   • Swagger UI: http://localhost:8080/api/swagger-ui.html"
    echo "   • Tipos Conectores: http://localhost:8080/api/connectors/types"
    echo "   • MongoDB: mongodb://admin:password123@localhost:27017"
    echo "   • Redis: redis://localhost:6379 (senha: redis123)"
    echo ""
    echo "🛑 Para parar: Ctrl+C"
    echo ""
    
    # Executar com Spring Boot
    mvn spring-boot:run
}

# Função para executar testes funcionais
run_functional_tests() {
    log_info "Executando testes funcionais..."
    
    if [ -f "run-tests.py" ]; then
        python3 run-tests.py --url http://localhost:8080/api
    else
        log_warning "Script de testes funcionais não encontrado"
    fi
}

# Função de limpeza
cleanup() {
    log_info "Fazendo limpeza..."
    docker-compose down
    log_success "Limpeza concluída"
}

# Função principal
main() {
    echo "🎯 Escolha uma opção:"
    echo "1) Setup completo (infra + build + run)"
    echo "2) Apenas infraestrutura"
    echo "3) Apenas build"
    echo "4) Apenas executar"
    echo "5) Testes funcionais"
    echo "6) Limpeza"
    echo "0) Sair"
    echo ""
    read -p "Opção: " choice
    
    case $choice in
        1)
            check_prerequisites
            setup_infrastructure
            build_application
            run_application
            ;;
        2)
            check_prerequisites
            setup_infrastructure
            ;;
        3)
            check_prerequisites
            build_application
            ;;
        4)
            run_application
            ;;
        5)
            run_functional_tests
            ;;
        6)
            cleanup
            ;;
        0)
            log_info "Saindo..."
            exit 0
            ;;
        *)
            log_error "Opção inválida"
            exit 1
            ;;
    esac
}

# Trap para limpeza em caso de interrupção
trap cleanup EXIT

# Executar função principal
main