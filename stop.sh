#!/bin/bash

# ============================================================================
# STOP.SH - Stop TOTVS Integration Hub Services
# Usage: ./stop.sh [--clean] [--remove-volumes]
# ============================================================================

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

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

# Parse arguments
CLEAN=false
REMOVE_VOLUMES=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN=true
            shift
            ;;
        --remove-volumes)
            REMOVE_VOLUMES=true
            shift
            ;;
        *)
            shift
            ;;
    esac
done

print_header "Stopping TOTVS Integration Hub"

# Stop containers
echo "Stopping containers..."
docker-compose down

if [ "$REMOVE_VOLUMES" = true ]; then
    print_warning "Removing volumes (data will be deleted)..."
    docker-compose down -v
    print_success "Volumes removed"
fi

if [ "$CLEAN" = true ]; then
    print_warning "Removing Docker images..."
    docker rmi $(docker images -q totvs-integration-hub) 2>/dev/null || true
    print_success "Cleanup complete"
fi

print_success "Services stopped"

echo ""
echo "To start services again, run:"
echo "  ./start.sh"