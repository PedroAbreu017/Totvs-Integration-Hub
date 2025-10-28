#!/bin/bash

# ============================================================================
# VERIFY.SH - Verify TOTVS Integration Hub Services Health
# Usage: ./verify.sh
# ============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Counters
PASSED=0
FAILED=0

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

check_service() {
    local service=$1
    local check_cmd=$2
    local description=$3
    
    echo -n "Checking $description... "
    
    if eval "$check_cmd" &> /dev/null; then
        echo -e "${GREEN}✓ PASS${NC}"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗ FAIL${NC}"
        ((FAILED++))
        return 1
    fi
}

check_docker_service() {
    local container=$1
    local name=$2
    
    echo -n "Checking $name... "
    
    if docker ps --filter "name=$container" --filter "status=running" | grep -q "$container"; then
        echo -e "${GREEN}✓ RUNNING${NC}"
        ((PASSED++))
    else
        echo -e "${RED}✗ NOT RUNNING${NC}"
        ((FAILED++))
    fi
}

check_port() {
    local port=$1
    local service=$2
    
    echo -n "Checking port $port ($service)... "
    
    if nc -z localhost $port 2>/dev/null; then
        echo -e "${GREEN}✓ OPEN${NC}"
        ((PASSED++))
    else
        echo -e "${RED}✗ CLOSED${NC}"
        ((FAILED++))
    fi
}

check_http_endpoint() {
    local url=$1
    local description=$2
    
    echo -n "Checking $description... "
    
    http_code=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null || echo "000")
    
    if [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✓ OK (HTTP $http_code)${NC}"
        ((PASSED++))
    else
        echo -e "${RED}✗ FAILED (HTTP $http_code)${NC}"
        ((FAILED++))
    fi
}

print_header "TOTVS Integration Hub - Service Verification"

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker is not installed or not in PATH${NC}"
    exit 1
fi

echo -e "${BLUE}1. Docker Containers${NC}"
check_docker_service "integration-postgres" "PostgreSQL"
check_docker_service "integration-redis" "Redis"
check_docker_service "integration-pgadmin" "PgAdmin"

echo ""
echo -e "${BLUE}2. Port Availability${NC}"
check_port "5433" "PostgreSQL"
check_port "6379" "Redis"
check_port "8081" "PgAdmin"
check_port "8080" "Application"

echo ""
echo -e "${BLUE}3. Database Connectivity${NC}"

# PostgreSQL
echo -n "PostgreSQL connection... "
if docker exec integration-postgres pg_isready -U postgres &> /dev/null; then
    echo -e "${GREEN}✓ CONNECTED${NC}"
    ((PASSED++))
else
    echo -e "${RED}✗ FAILED${NC}"
    ((FAILED++))
fi

# Redis
echo -n "Redis connection... "
if docker exec integration-redis redis-cli ping | grep -q "PONG"; then
    echo -e "${GREEN}✓ CONNECTED${NC}"
    ((PASSED++))
else
    echo -e "${RED}✗ FAILED${NC}"
    ((FAILED++))
fi

echo ""
echo -e "${BLUE}4. Application Health${NC}"
check_http_endpoint "http://localhost:8080/actuator/health" "Application health check"
check_http_endpoint "http://localhost:8080/api/v1/health" "Custom health endpoint"
check_http_endpoint "http://localhost:8080/swagger-ui.html" "Swagger UI"

echo ""
echo -e "${BLUE}5. Database Tables${NC}"

# Check if tables exist
echo -n "Database tables... "
tables=$(docker exec integration-postgres psql -U postgres -d integration_hub -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" 2>/dev/null || echo "0")

if [ "$tables" -gt 0 ]; then
    echo -e "${GREEN}✓ FOUND ($tables tables)${NC}"
    ((PASSED++))
else
    echo -e "${YELLOW}⚠ NO TABLES (application needs to initialize)${NC}"
fi

echo ""
print_header "Verification Summary"

echo -e "Passed: ${GREEN}$PASSED${NC}"
echo -e "Failed: ${RED}$FAILED${NC}"

if [ $FAILED -eq 0 ]; then
    echo ""
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
    echo -e "${GREEN}All services are healthy! ✓${NC}"
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
    echo ""
    echo "Quick Links:"
    echo "  API Docs:   http://localhost:8080/swagger-ui.html"
    echo "  Health:     http://localhost:8080/actuator/health"
    echo "  PgAdmin:    http://localhost:8081"
    echo "  PostgreSQL: localhost:5433"
    echo "  Redis:      localhost:6379"
    echo ""
    exit 0
else
    echo ""
    echo -e "${RED}═══════════════════════════════════════${NC}"
    echo -e "${RED}Some services are not healthy! ✗${NC}"
    echo -e "${RED}═══════════════════════════════════════${NC}"
    echo ""
    echo "Troubleshooting:"
    echo "1. Check Docker daemon is running"
    echo "2. View container logs: docker-compose logs -f"
    echo "3. Restart services: docker-compose restart"
    echo "4. Full cleanup: docker-compose down -v && docker-compose up -d"
    echo ""
    exit 1
fi