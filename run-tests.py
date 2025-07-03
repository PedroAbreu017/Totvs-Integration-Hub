#!/usr/bin/env python3
"""
Script automático para testar TOTVS Integration Hub
Executa testes funcionais e de performance
"""

import requests
import json
import time
import sys
from datetime import datetime
from typing import Dict, List, Any

class TotvsTester:
    def __init__(self, base_url: str = "http://localhost:8080/api"):
        self.base_url = base_url
        self.session = requests.Session()
        self.results = {
            'passed': 0,
            'failed': 0,
            'errors': [],
            'performance': {}
        }
    
    def log(self, message: str, status: str = "INFO"):
        timestamp = datetime.now().strftime("%H:%M:%S")
        icons = {"INFO": "ℹ️", "PASS": "✅", "FAIL": "❌", "PERF": "⚡"}
        print(f"{icons.get(status, 'ℹ️')} [{timestamp}] {message}")
    
    def assert_test(self, condition: bool, test_name: str, details: str = ""):
        if condition:
            self.results['passed'] += 1
            self.log(f"{test_name} - {details}", "PASS")
        else:
            self.results['failed'] += 1
            error_msg = f"{test_name} - {details}"
            self.results['errors'].append(error_msg)
            self.log(error_msg, "FAIL")
        return condition
    
    def measure_performance(self, func, test_name: str):
        """Mede performance de uma função"""
        start_time = time.time()
        result = func()
        duration = (time.time() - start_time) * 1000  # ms
        self.results['performance'][test_name] = duration
        self.log(f"{test_name}: {duration:.2f}ms", "PERF")
        return result, duration
    
    def test_health_check(self):
        """Testa health check da aplicação"""
        self.log("Testando Health Check...")
        
        def health_request():
            response = self.session.get(f"{self.base_url}/v1/health")
            return response
        
        response, duration = self.measure_performance(health_request, "Health Check")
        
        # Validações básicas
        self.assert_test(response.status_code == 200, "Health Status Code", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test(data.get('success') == True, "Health Success Flag", "success=true")
            
            health_data = data.get('data', {})
            self.assert_test(health_data.get('status') == 'UP', "Health Status", "status=UP")
            
            # Verificar componentes
            components = health_data.get('components', {})
            self.assert_test('mongodb' in components, "MongoDB Component", "presente")
            self.assert_test('redis' in components, "Redis Component", "presente")
            
            # Verificar status dos componentes
            if 'mongodb' in components:
                mongo_status = components['mongodb'].get('status')
                self.assert_test(mongo_status == 'UP', "MongoDB Status", f"status={mongo_status}")
            
            if 'redis' in components:
                redis_status = components['redis'].get('status')
                self.assert_test(redis_status == 'UP', "Redis Status", f"status={redis_status}")
        
        # Performance
        self.assert_test(duration < 100, "Health Performance", f"{duration:.2f}ms < 100ms")
    
    def test_connector_types(self):
        """Testa listagem de tipos de conectores"""
        self.log("Testando Tipos de Conectores...")
        
        def types_request():
            return self.session.get(f"{self.base_url}/v1/connectors/types")
        
        response, duration = self.measure_performance(types_request, "Connector Types")
        
        self.assert_test(response.status_code == 200, "Types Status Code", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test(data.get('success') == True, "Types Success Flag", "success=true")
            
            types = data.get('data', [])
            self.assert_test(isinstance(types, list), "Types Data Type", "é lista")
            self.assert_test(len(types) >= 8, "Types Count", f"{len(types)} tipos ≥ 8")
            
            # Verificar tipos específicos
            expected_types = ['REST', 'DATABASE_POSTGRESQL', 'DATABASE_MONGODB', 'FILE_CSV']
            for expected_type in expected_types:
                self.assert_test(expected_type in types, f"Type {expected_type}", "presente")
        
        self.assert_test(duration < 200, "Types Performance", f"{duration:.2f}ms < 200ms")
    
    def test_connector_schema(self):
        """Testa schema de conector PostgreSQL"""
        self.log("Testando Schema PostgreSQL...")
        
        def schema_request():
            return self.session.get(f"{self.base_url}/v1/connectors/DATABASE_POSTGRESQL/schema")
        
        response, duration = self.measure_performance(schema_request, "PostgreSQL Schema")
        
        self.assert_test(response.status_code == 200, "Schema Status Code", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test(data.get('success') == True, "Schema Success Flag", "success=true")
            
            schema = data.get('data', {})
            self.assert_test('type' in schema, "Schema Type", "presente")
            self.assert_test('required' in schema, "Schema Required", "presente")
            self.assert_test('properties' in schema, "Schema Properties", "presente")
            
            required_fields = schema.get('required', [])
            expected_required = ['host', 'port', 'database', 'username', 'password']
            for field in expected_required:
                self.assert_test(field in required_fields, f"Required Field {field}", "presente")
    
    def test_connector_validation_valid(self):
        """Testa validação com configuração válida"""
        self.log("Testando Validação Válida...")
        
        valid_config = {
            "connectorConfig": {
                "type": "DATABASE_POSTGRESQL",
                "name": "Test PostgreSQL",
                "host": "localhost",
                "port": 5432,
                "database": "testdb",
                "username": "testuser",
                "password": "testpass"
            }
        }
        
        def validation_request():
            return self.session.post(
                f"{self.base_url}/v1/connectors/validate",
                json=valid_config
            )
        
        response, duration = self.measure_performance(validation_request, "Valid Validation")
        
        self.assert_test(response.status_code == 200, "Validation Status Code", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test(data.get('success') == True, "Validation Success Flag", "success=true")
            
            validation_data = data.get('data', {})
            self.assert_test(validation_data.get('valid') == True, "Config Valid", "valid=true")
            
            errors = validation_data.get('errors', [])
            self.assert_test(len(errors) == 0, "Validation Errors", "sem erros")
        
        self.assert_test(duration < 300, "Validation Performance", f"{duration:.2f}ms < 300ms")
    
    def test_connector_validation_invalid(self):
        """Testa validação com configuração inválida"""
        self.log("Testando Validação Inválida...")
        
        invalid_config = {
            "connectorConfig": {
                "type": "DATABASE_POSTGRESQL",
                "name": "Test Invalid",
                "host": "localhost"
                # port, database, username, password ausentes
            }
        }
        
        def validation_request():
            return self.session.post(
                f"{self.base_url}/v1/connectors/validate",
                json=invalid_config
            )
        
        response, duration = self.measure_performance(validation_request, "Invalid Validation")
        
        self.assert_test(response.status_code == 200, "Invalid Validation Status", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test(data.get('success') == True, "Invalid Success Flag", "success=true")
            
            validation_data = data.get('data', {})
            self.assert_test(validation_data.get('valid') == False, "Config Invalid", "valid=false")
            
            errors = validation_data.get('errors', [])
            self.assert_test(len(errors) > 0, "Validation Errors Present", f"{len(errors)} erros")
    
    def test_connector_templates(self):
        """Testa templates de conectores"""
        self.log("Testando Templates...")
        
        def templates_request():
            return self.session.get(f"{self.base_url}/v1/connectors/templates")
        
        response, duration = self.measure_performance(templates_request, "Templates")
        
        self.assert_test(response.status_code == 200, "Templates Status Code", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test(data.get('success') == True, "Templates Success Flag", "success=true")
            
            templates = data.get('data', {})
            self.assert_test(isinstance(templates, dict), "Templates Data Type", "é dict")
            
            # Verificar templates específicos
            expected_templates = ['DATABASE_POSTGRESQL', 'REST', 'DATABASE_MONGODB', 'FILE_CSV']
            for template in expected_templates:
                self.assert_test(template in templates, f"Template {template}", "presente")
                
                if template in templates:
                    template_data = templates[template]
                    self.assert_test('type' in template_data, f"Template {template} Type", "tem type")
                    self.assert_test('name' in template_data, f"Template {template} Name", "tem name")
    
    def test_system_endpoints(self):
        """Testa endpoints do sistema"""
        self.log("Testando Endpoints do Sistema...")
        
        # Test version endpoint
        response = self.session.get(f"{self.base_url}/v1/system/version")
        self.assert_test(response.status_code == 200, "System Version", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test(data.get('success') == True, "Version Success", "success=true")
            
            version_data = data.get('data', {})
            self.assert_test('name' in version_data, "Version Name", "presente")
            self.assert_test('version' in version_data, "Version Number", "presente")
            
            name = version_data.get('name', '')
            self.assert_test('TOTVS' in name, "Application Name", f"contém TOTVS: {name}")
        
        # Test ping endpoint
        response = self.session.get(f"{self.base_url}/test/ping")
        self.assert_test(response.status_code == 200, "Test Ping", "Status 200")
        
        if response.status_code == 200:
            data = response.json()
            self.assert_test('message' in data, "Ping Message", "presente")
            self.assert_test('status' in data, "Ping Status", "presente")
    
    def test_performance_requirements(self):
        """Testa requisitos de performance geral"""
        self.log("Testando Performance Geral...")
        
        # Testar múltiplas chamadas para health check
        total_time = 0
        calls = 5
        
        for i in range(calls):
            start_time = time.time()
            response = self.session.get(f"{self.base_url}/v1/health")
            duration = (time.time() - start_time) * 1000
            total_time += duration
            
            self.assert_test(response.status_code == 200, f"Health Call {i+1}", "Status 200")
            self.assert_test(duration < 150, f"Health Performance {i+1}", f"{duration:.2f}ms")
        
        avg_time = total_time / calls
        self.assert_test(avg_time < 100, "Average Health Performance", f"{avg_time:.2f}ms < 100ms")
        
        self.log(f"Performance média: {avg_time:.2f}ms ({calls} chamadas)", "PERF")
    
    def test_error_handling(self):
        """Testa tratamento de erros"""
        self.log("Testando Tratamento de Erros...")
        
        # Teste endpoint inexistente
        response = self.session.get(f"{self.base_url}/v1/nonexistent")
        self.assert_test(response.status_code == 404, "Endpoint Inexistente", "Status 404")
        
        # Teste conector inválido
        response = self.session.get(f"{self.base_url}/v1/connectors/INVALID_TYPE/schema")
        self.assert_test(response.status_code in [400, 404], "Conector Inválido", f"Status {response.status_code}")
        
        # Teste JSON malformado
        try:
            response = self.session.post(
                f"{self.base_url}/v1/connectors/validate",
                data="invalid json",
                headers={'Content-Type': 'application/json'}
            )
            self.assert_test(response.status_code == 400, "JSON Malformado", "Status 400")
        except:
            self.assert_test(True, "JSON Malformado", "erro capturado")
    
    def run_all_tests(self):
        """Executa todos os testes"""
        start_time = time.time()
        self.log("🧪 Iniciando Testes TOTVS Integration Hub", "INFO")
        self.log(f"Base URL: {self.base_url}", "INFO")
        
        try:
            # Verificar se aplicação está rodando
            try:
                response = self.session.get(f"{self.base_url}/v1/health", timeout=5)
                if response.status_code != 200:
                    self.log("❌ Aplicação não está respondendo. Verifique se está rodando.", "FAIL")
                    return False
            except requests.exceptions.RequestException:
                self.log("❌ Não foi possível conectar à aplicação. Verifique se está rodando.", "FAIL")
                return False
            
            # Executar testes
            self.test_health_check()
            self.test_connector_types()
            self.test_connector_schema()
            self.test_connector_validation_valid()
            self.test_connector_validation_invalid()
            self.test_connector_templates()
            self.test_system_endpoints()
            self.test_performance_requirements()
            self.test_error_handling()
            
            # Relatório final
            total_time = time.time() - start_time
            total_tests = self.results['passed'] + self.results['failed']
            
            self.log("📊 RELATÓRIO FINAL", "INFO")
            self.log(f"Testes executados: {total_tests}", "INFO")
            self.log(f"Sucessos: {self.results['passed']}", "PASS")
            self.log(f"Falhas: {self.results['failed']}", "FAIL" if self.results['failed'] > 0 else "INFO")
            self.log(f"Tempo total: {total_time:.2f}s", "INFO")
            
            if self.results['failed'] > 0:
                self.log("❌ ERROS ENCONTRADOS:", "FAIL")
                for error in self.results['errors']:
                    self.log(f"  • {error}", "FAIL")
            
            # Performance summary
            if self.results['performance']:
                self.log("⚡ RESUMO DE PERFORMANCE:", "PERF")
                for test, duration in self.results['performance'].items():
                    status = "✅" if duration < 200 else "⚠️" if duration < 500 else "❌"
                    self.log(f"  {status} {test}: {duration:.2f}ms", "PERF")
            
            success_rate = (self.results['passed'] / total_tests * 100) if total_tests > 0 else 0
            self.log(f"Taxa de sucesso: {success_rate:.1f}%", "PASS" if success_rate > 95 else "FAIL")
            
            if self.results['failed'] == 0:
                self.log("🎉 TODOS OS TESTES PASSARAM! Aplicação está funcionando perfeitamente!", "PASS")
                return True
            else:
                self.log("⚠️ Alguns testes falharam. Verifique os erros acima.", "FAIL")
                return False
                
        except Exception as e:
            self.log(f"Erro durante execução dos testes: {str(e)}", "FAIL")
            return False

def main():
    """Função principal"""
    import argparse
    
    parser = argparse.ArgumentParser(description='Tester para TOTVS Integration Hub')
    parser.add_argument('--url', default='http://localhost:8080/api', 
                       help='URL base da API (default: http://localhost:8080/api)')
    parser.add_argument('--verbose', action='store_true', 
                       help='Output verboso')
    
    args = parser.parse_args()
    
    tester = TotvsTester(args.url)
    success = tester.run_all_tests()
    
    # Exit code para CI/CD
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main()