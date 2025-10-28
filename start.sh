#!/bin/bash

# ============================================================================
# START.SH - Start TOTVS Integration Hub with Docker
# Usage: ./start.sh [dev|prod]
# ============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
ENVIRONMENT=${1:-dev}
DOCKER_COMPOSE_FILE="docker-compose.yml"

# Functions
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

check_requirements() {
    print_header "Checking Requirements"
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    print_success "Docker is installed"
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    print_success "Docker Compose is installed"
    
    # Check if docker-compose file exists
    if [ ! -f "$DOCKER_COMPOSE_FILE" ]; then
        print_error "docker-compose.yml not found in current directory"
        exit 1
    fi
    print_success "docker-compose.yml found"
}

start_docker_services() {
    print_header "Starting Docker Services"
    
    # Stop existing containers (if any)
    echo "Stopping existing containers..."
    docker-compose down 2>/dev/null || true
    
    # Start services
    echo "Starting PostgreSQL, Redis, and PgAdmin..."
    docker-compose up -d
    
    # Wait for services to be ready
    echo "Waiting for services to be ready..."
    sleep 5
    
    # Check PostgreSQL
    echo -n "Checking PostgreSQL... "
    for i in {1..30}; do
        if docker exec integration-postgres pg_isready -U postgres &> /dev/null; then
            print_success "PostgreSQL is ready"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "PostgreSQL failed to start"
            exit 1
        fi
        echo -n "."
        sleep 1
    done
    
    # Check Redis
    echo -n "Checking Redis... "
    for i in {1..30}; do
        if docker exec integration-redis redis-cli ping &> /dev/null; then
            print_success "Redis is ready"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "Redis failed to start"
            exit 1
        fi
        echo -n "."
        sleep 1
    done
}

start_application() {
    print_header "Starting Application"
    
    # Check if Maven is installed
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven is not installed. You need to run:"
        echo "  mvn clean package"
        echo "  java -jar target/integration-prototype-1.0.0-SNAPSHOT.jar"
        return
    fi
    
    # Build and start with Maven
    echo "Building application with Maven..."
    mvn clean spring-boot:run -DskipTests -P$ENVIRONMENT
}

show_services_info() {
    print_header "Services Information"
    
    echo -e "${BLUE}PostgreSQL:${NC}"
    echo "  Host: localhost:5433"
    echo "  Database: integration_hub"
    echo "  User: postgres"
    echo "  Password: postgres"
    echo ""
    
    echo -e "${BLUE}Redis:${NC}"
    echo "  Host: localhost:6379"
    echo ""
    
    echo -e "${BLUE}PgAdmin:${NC}"
    echo "  URL: http://localhost:8081"
    echo "  Email: admin@totvs.com"
    echo "  Password: admin123"
    echo ""
    
    echo -e "${BLUE}Application:${NC}"
    echo "  URL: http://localhost:8080"
    echo "  API Docs: http://localhost:8080/swagger-ui.html"
    echo "  Health: http://localhost:8080/actuator/health"
}

show_troubleshooting() {
    print_header "Troubleshooting"
    
    echo "If services fail to start:"
    echo ""
    echo "1. Check Docker is running:"
    echo "   docker ps"
    echo ""
    echo "2. View logs:"
    echo "   docker-compose logs -f postgres"
    echo "   docker-compose logs -f redis"
    echo ""
    echo "3. Stop and clean up:"
    echo "   docker-compose down -v"
    echo ""
    echo "4. Rebuild everything:"
    echo "   docker-compose up --build"
}

# Main execution
main() {
    clear
    
    echo -e "${BLUE}"
    echo "╔════════════════════════════════════════╗"
    echo "║  TOTVS Integration Hub - Start Script  ║"
    echo "║  Environment: $ENVIRONMENT                     ║"
    echo "╚════════════════════════════════════════╝"
    echo -e "${NC}"
    
    check_requirements
    start_docker_services
    show_services_info
    
    # Try to start application
    echo ""
    read -p "Start application now? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        start_application
    else
        print_success "Docker services are running!"
        print_warning "To start the application manually, run:"
        echo "  mvn clean spring-boot:run"
        echo "  or"
        echo "  java -jar target/integration-prototype-1.0.0-SNAPSHOT.jar"
    fi
}

# Handle Ctrl+C
trap 'echo -e "\n${YELLOW}Interrupted. Services are still running.${NC}"; exit 0' INT

# Run main function
main