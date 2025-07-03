# ============================================================================
# scripts/build.ps1 - Build do projeto
# ============================================================================
Write-Host "🔨 Building TOTVS Integration Hub..." -ForegroundColor Green

try {
    Write-Host "Cleaning and compiling..." -ForegroundColor Yellow
    mvn clean compile -DskipTests
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Build completed successfully!" -ForegroundColor Green
    } else {
        throw "Build failed"
    }
} catch {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "To run the application:" -ForegroundColor Cyan
Write-Host "mvn spring-boot:run" -ForegroundColor White

# ============================================================================
# scripts/test.ps1 - Executar testes
# ============================================================================
Write-Host "🧪 Running tests for TOTVS Integration Hub..." -ForegroundColor Green

try {
    Write-Host "Running unit tests..." -ForegroundColor Yellow
    mvn test
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ All tests passed!" -ForegroundColor Green
    } else {
        throw "Tests failed"
    }
} catch {
    Write-Host "❌ Some tests failed!" -ForegroundColor Red
    exit 1
}