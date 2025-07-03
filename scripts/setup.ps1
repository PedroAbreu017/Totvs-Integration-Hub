# ============================================================================
# scripts/setup.ps1 - Setup do ambiente (Windows PowerShell)
# ============================================================================
Write-Host "🚀 Setting up TOTVS Integration Hub..." -ForegroundColor Green

# Verificar pré-requisitos
Write-Host "Checking prerequisites..." -ForegroundColor Yellow

try {
    $javaVersion = java --version 2>$null
    if ($javaVersion) {
        Write-Host "✅ Java found" -ForegroundColor Green
    } else {
        throw "Java not found"
    }
} catch {
    Write-Host "❌ Java 17+ required" -ForegroundColor Red
    exit 1
}

try {
    $dockerVersion = docker --version 2>$null
    if ($dockerVersion) {
        Write-Host "✅ Docker found" -ForegroundColor Green
    } else {
        throw "Docker not found"
    }
} catch {
    Write-Host "❌ Docker required" -ForegroundColor Red
    exit 1
}

try {
    $mvnVersion = mvn --version 2>$null
    if ($mvnVersion) {
        Write-Host "✅ Maven found" -ForegroundColor Green
    } else {
        throw "Maven not found"
    }
} catch {
    Write-Host "❌ Maven required" -ForegroundColor Red
    exit 1
}

# Criar diretórios necessários
Write-Host "Creating project directories..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path "logs" | Out-Null
New-Item -ItemType Directory -Force -Path "data\mongodb" | Out-Null
New-Item -ItemType Directory -Force -Path "data\redis" | Out-Null
New-Item -ItemType Directory -Force -Path "data\postgres" | Out-Null

# Inicializar arquivos de dados
Write-Host "Setting up data files..." -ForegroundColor Yellow

# Criar arquivo de inicialização do MongoDB
$mongoInit = @"
// Criar usuário e database para integração
db = db.getSiblingDB('integration_hub');

db.createUser({
  user: 'integration_user',
  pwd: 'integration_pass',
  roles: [
    {
      role: 'readWrite',
      db: 'integration_hub'
    }
  ]
});

// Criar coleções iniciais
db.createCollection('tenants');
db.createCollection('integrations');
db.createCollection('execution_logs');
db.createCollection('tenant_stats');

// Inserir tenant padrão para desenvolvimento
db.tenants.insertOne({
  _id: ObjectId(),
  tenantId: 'default',
  name: 'Default Tenant',
  description: 'Tenant padrão para desenvolvimento',
  contactEmail: 'admin@totvs.com.br',
  apiKey: 'dev-api-key-123',
  status: 'ACTIVE',
  plan: 'ENTERPRISE',
  maxRequestsPerMinute: 1000,
  maxConcurrentIntegrations: 50,
  settings: {},
  createdAt: new Date(),
  updatedAt: new Date()
});

print('✅ MongoDB initialized successfully');
"@

$mongoInit | Out-File -FilePath "scripts\init-mongo.js" -Encoding UTF8

# Criar arquivo de inicialização do PostgreSQL
$postgresInit = @"
-- Criar schema e tabelas para testes de conectores
CREATE SCHEMA IF NOT EXISTS test_schema;

-- Tabela de exemplo para testes
CREATE TABLE IF NOT EXISTS test_schema.customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserir dados de exemplo
INSERT INTO test_schema.customers (name, email, phone) VALUES
('João Silva', 'joao@example.com', '(11) 99999-0001'),
('Maria Santos', 'maria@example.com', '(11) 99999-0002'),
('Pedro Oliveira', 'pedro@example.com', '(11) 99999-0003'),
('Ana Costa', 'ana@example.com', '(11) 99999-0004'),
('Carlos Lima', 'carlos@example.com', '(11) 99999-0005')
ON CONFLICT (email) DO NOTHING;

-- Criar usuário para aplicação
CREATE USER IF NOT EXISTS integration_user WITH PASSWORD 'integration_pass';
GRANT ALL PRIVILEGES ON SCHEMA test_schema TO integration_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA test_schema TO integration_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA test_schema TO integration_user;

SELECT '✅ PostgreSQL initialized successfully' as status;
"@

$postgresInit | Out-File -FilePath "scripts\init-postgres.sql" -Encoding UTF8

# Criar arquivo de inicialização do MySQL
$mysqlInit = @"
-- Criar database e tabelas para testes
CREATE DATABASE IF NOT EXISTS test_database;
USE test_database;

-- Tabela de exemplo para testes
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    category VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inserir dados de exemplo
INSERT IGNORE INTO products (code, name, description, price, category) VALUES
('PROD001', 'Notebook Dell', 'Notebook Dell Inspiron 15', 2500.00, 'Informática'),
('PROD002', 'Mouse Logitech', 'Mouse óptico sem fio', 89.90, 'Periféricos'),
('PROD003', 'Teclado Mecânico', 'Teclado mecânico RGB', 299.99, 'Periféricos'),
('PROD004', 'Monitor LG', 'Monitor 24" Full HD', 899.00, 'Monitores'),
('PROD005', 'Impressora HP', 'Impressora multifuncional', 450.00, 'Impressoras');

SELECT '✅ MySQL initialized successfully' as status;
"@

$mysqlInit | Out-File -FilePath "scripts\init-mysql.sql" -Encoding UTF8

# Criar arquivo JSON para mock API
$mockApi = @"
{
  "users": [
    {
      "id": 1,
      "name": "João Silva",
      "email": "joao@example.com",
      "department": "TI",
      "active": true
    },
    {
      "id": 2,
      "name": "Maria Santos", 
      "email": "maria@example.com",
      "department": "Vendas",
      "active": true
    },
    {
      "id": 3,
      "name": "Pedro Oliveira",
      "email": "pedro@example.com", 
      "department": "Marketing",
      "active": false
    }
  ],
  "orders": [
    {
      "id": 1,
      "userId": 1,
      "product": "Notebook",
      "quantity": 1,
      "total": 2500.00,
      "status": "completed"
    },
    {
      "id": 2,
      "userId": 2,
      "product": "Mouse",
      "quantity": 2,
      "total": 179.80,
      "status": "pending"
    }
  ]
}
"@

$mockApi | Out-File -FilePath "scripts\mock-api.json" -Encoding UTF8

Write-Host "✅ Setup completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Run: docker-compose up -d (start infrastructure)" -ForegroundColor White
Write-Host "2. Run: mvn clean compile (build project)" -ForegroundColor White
Write-Host "3. Run: mvn spring-boot:run (start application)" -ForegroundColor White
Write-Host ""
Write-Host "Access points:" -ForegroundColor Cyan
Write-Host "- API: http://localhost:8080/api" -ForegroundColor White
Write-Host "- Swagger: http://localhost:8080/api/swagger-ui.html" -ForegroundColor White
Write-Host "- Adminer (DB): http://localhost:8081" -ForegroundColor White
Write-Host "- Redis Commander: http://localhost:8082" -ForegroundColor White

