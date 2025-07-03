package com.totvs.integration.controller;

import com.totvs.integration.dto.request.TestConnectorRequest;
import com.totvs.integration.dto.response.ConnectorTestResponse;
import com.totvs.integration.service.ConnectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/connectors")
@RequiredArgsConstructor
public class ConnectorController {

    private final ConnectorService connectorService;

    @GetMapping("/types")
    public ResponseEntity<List<String>> getAvailableTypes() {
        log.debug("Buscando tipos de conectores disponíveis");
        
        List<String> types = connectorService.getAvailableConnectorTypes();
        
        return ResponseEntity.ok(types);
    }

    @PostMapping("/test")
    public ResponseEntity<ConnectorTestResponse> testConnector(
            @Valid @RequestBody TestConnectorRequest request) {
        
        log.info("Testando conector tipo: {}", request.getType());
        
        ConnectorTestResponse response = connectorService.testConnector(request);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/templates")
    public ResponseEntity<Map<String, Object>> getConnectorTemplates(
            @RequestParam(required = false) String type) {
        
        log.debug("Buscando templates para tipo: {}", type);
        
        Map<String, Object> templates = connectorService.getConnectorTemplates(type);
        
        return ResponseEntity.ok(templates);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateConnectorConfig(
            @Valid @RequestBody TestConnectorRequest request) {
        
        log.info("Validando configuração do conector tipo: {}", request.getType());
        
        List<String> errors = connectorService.validateConnectorConfig(request.getType(), request.getConfiguration());
        boolean isValid = errors.isEmpty();
        
        Map<String, Object> response = Map.of(
            "valid", isValid,
            "type", request.getType(),
            "errors", errors,
            "message", isValid ? "Configuração válida" : "Configuração inválida"
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{type}/schema")
    public ResponseEntity<Map<String, Object>> getConnectorSchema(@PathVariable String type) {
        log.debug("Buscando schema para tipo: {}", type);

        List<String> validTypes = connectorService.getAvailableConnectorTypes();
        if (!validTypes.contains(type.toUpperCase())) {
            log.warn("Tipo de conector inválido: {}", type);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de conector inválido: " + type);
        }
        
        Map<String, Object> schema = connectorService.getConnectorSchema(type);
        
        return ResponseEntity.ok(schema);
    }
}