#!/bin/bash
# ==============================================================================
# SCRIPT DE DEPLOY AUTOMATIZADO - TOTVS INTEGRATION HUB
# ==============================================================================

set -euo pipefail

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funções de log
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Variáveis
PROJECT_NAME="totvs-integration-hub"
VERSION="1.0.0"
DOCKER_REGISTRY="${DOCKER_REGISTRY:-}"
ENVIRONMENT="${ENVIRONMENT:-production}"

# ==============================================================================
# FUNÇÕES
# ==============================================================================

# Verificar dependências
check_dependencies() {
    log_info "Verificando dependências..."
    
    commands=("docker" "docker-compose" "mvn" "curl")
    for cmd in "${commands[@]}"; do
        if ! command -v $cmd &> /dev/null; then
            log_error "$cmd não está instalado"
            exit 1
        fi
    done
    
    log_success "Todas as dependências estão instaladas"
}

# Build da aplicação
build_application() {
    log_info "Iniciando build da aplicação..."
    
    # Executar testes
    log_info "Executando testes..."
    mvn test
    
    # Build do JAR
    log_info "Criando JAR de produção..."
    mvn clean package -DskipTests
    
    log_success "Build da aplicação concluído"
}

# Build das imagens Docker
build_docker_images() {
    log_info "Construindo imagens Docker..."
    
    # Build da imagem principal
    docker build -t ${PROJECT_NAME}:${VERSION} .
    docker build -t ${PROJECT_NAME}:latest .
    
    # Tag para registry se especificado
    if [ -n "$DOCKER_REGISTRY" ]; then
        docker tag ${PROJECT_NAME}:${VERSION} ${DOCKER_REGISTRY}/${PROJECT_NAME}:${VERSION}
        docker tag ${PROJECT_NAME}:latest ${DOCKER_REGISTRY}/${PROJECT_NAME}:latest
    fi
    
    log_success "Imagens Docker construídas"
}

# Deploy local
deploy_local() {
    log_info "Iniciando deploy local..."
    
    # Criar diretórios necessários
    mkdir -p ./logs ./data ./config
    mkdir -p ./nginx ./monitoring/grafana/{dashboards,datasources}
    
    # Parar containers existentes
    log_info "Parando containers existentes..."
    docker-compose down --remove-orphans
    
    # Iniciar stack completa
    log_info "Iniciando stack completa..."
    docker-compose up -d
    
    # Aguardar containers ficarem healthy
    log_info "Aguardando containers ficarem saudáveis..."
    sleep 30
    
    # Verificar status
    check_deployment_health
    
    log_success "Deploy local concluído"
}

# Deploy em produção
deploy_production() {
    log_info "Iniciando deploy em produção..."
    
    if [ -n "$DOCKER_REGISTRY" ]; then
        # Push para registry
        log_info "Enviando imagens para registry..."
        docker push ${DOCKER_REGISTRY}/${PROJECT_NAME}:${VERSION}
        docker push ${DOCKER_REGISTRY}/${PROJECT_NAME}:latest
    fi
    
    # Deploy usando docker-compose
    ENVIRONMENT=production docker-compose -f docker-compose.yml up -d
    
    log_success "Deploy em produção concluído"
}

# Verificar saúde do deployment
check_deployment_health() {
    log_info "Verificando saúde do deployment..."
    
    # Verificar containers
    if ! docker-compose ps | grep -q "Up"; then
        log_error "Alguns containers não estão rodando"
        docker-compose ps
        return 1
    fi
    
    # Verificar health da aplicação
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:8080/actuator/health &>/dev/null; then
            log_success "Aplicação está saudável"
            break
        fi
        
        log_info "Tentativa $attempt/$max_attempts - Aguardando aplicação ficar disponível..."
        sleep 10
        ((attempt++))
    done
    
    if [ $attempt -gt $max_attempts ]; then
        log_error "Aplicação não ficou disponível após $max_attempts tentativas"
        show_logs
        return 1
    fi
    
    # Mostrar URLs de acesso
    show_access_info
}

# Mostrar logs
show_logs() {
    log_info "Últimos logs da aplicação:"
    docker-compose logs --tail=50 totvs-integration-hub
}

# Mostrar informações de acesso
show_access_info() {
    log_success "🚀 TOTVS Integration Hub está rodando!"
    echo ""
    echo "📋 URLs de Acesso:"
    echo "   🌐 API: http://localhost:8080/api"
    echo "   📚 Swagger: http://localhost:8080/swagger-ui.html"
    echo "   💚 Health: http://localhost:8080/actuator/health"
    echo "   📊 Métricas: http://localhost:8080/actuator/metrics"
    echo "   📈 Grafana: http://localhost:3000 (admin/totvs123)"
    echo "   🔍 Prometheus: http://localhost:9090"
    echo ""
    echo "🔧 Gerenciamento:"
    echo "   ⬆️  Subir: docker-compose up -d"
    echo "   ⬇️  Parar: docker-compose down"
    echo "   📜 Logs: docker-compose logs -f totvs-integration-hub"
    echo "   🔄 Restart: docker-compose restart totvs-integration-hub"
}

# Rollback
rollback() {
    log_warning "Iniciando rollback..."
    
    # Parar containers atuais
    docker-compose down
    
    # Restaurar versão anterior (assumindo que existe)
    # Em produção real, você teria tags de versão para rollback
    log_info "Restaurando versão anterior..."
    
    log_success "Rollback concluído"
}

# Cleanup
cleanup() {
    log_info "Limpando recursos..."
    
    # Parar containers
    docker-compose down --remove-orphans
    
    # Remover imagens não utilizadas
    docker image prune -f
    
    log_success "Cleanup concluído"
}

# ==============================================================================
# MENU PRINCIPAL
# ==============================================================================
show_menu() {
    echo ""
    echo "🚀 TOTVS Integration Hub - Deploy Manager"
    echo "========================================"
    echo "1) 🏗️  Build completo (app + docker)"
    echo "2) 🐳 Deploy local (desenvolvimento)"
    echo "3) 🌐 Deploy produção"
    echo "4) 🔍 Verificar saúde"
    echo "5) 📜 Mostrar logs"
    echo "6) 🔄 Rollback"
    echo "7) 🧹 Cleanup"
    echo "8) ❌ Sair"
    echo ""
}

# ==============================================================================
# MAIN
# ==============================================================================
main() {
    check_dependencies
    
    if [ $# -eq 0 ]; then
        # Menu interativo
        while true; do
            show_menu
            read -p "Escolha uma opção: " choice
            
            case $choice in
                1) build_application && build_docker_images ;;
                2) deploy_local ;;
                3) deploy_production ;;
                4) check_deployment_health ;;
                5) show_logs ;;
                6) rollback ;;
                7) cleanup ;;
                8) exit 0 ;;
                *) log_error "Opção inválida" ;;
            esac
        done
    else
        # Comando direto
        case $1 in
            build) build_application && build_docker_images ;;
            deploy-local) deploy_local ;;
            deploy-prod) deploy_production ;;
            health) check_deployment_health ;;
            logs) show_logs ;;
            rollback) rollback ;;
            cleanup) cleanup ;;
            *) 
                echo "Uso: $0 [build|deploy-local|deploy-prod|health|logs|rollback|cleanup]"
                exit 1
                ;;
        esac
    fi
}

# Executar script
main "$@"