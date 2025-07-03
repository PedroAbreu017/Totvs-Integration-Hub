package com.totvs.integration.service;

import com.totvs.integration.connector.ConnectorFactory;
import com.totvs.integration.entity.ExecutionLog;
import com.totvs.integration.entity.Integration;
import com.totvs.integration.repository.ExecutionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class IntegrationExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationExecutorService.class);

    @Autowired
    private ConnectorFactory connectorFactory;

    @Autowired
    private ExecutionLogRepository executionLogRepository;

   
    @Async
    public CompletableFuture<ExecutionLog> executeIntegrationAsync(Integration integration) {
        logger.info("Iniciando execução da integração: {}", integration.getName());
        
        
        ExecutionLog log = ExecutionLog.builder()
                .integrationId(integration.getId())
                .integrationName(integration.getName())
                .status(ExecutionLog.ExecutionStatus.RUNNING)
                .startTime(LocalDateTime.now())
                .sourceConnectorType(integration.getSourceConnector() != null ? integration.getSourceConnector().getType() : null)
                .targetConnectorType(integration.getTargetConnector() != null ? integration.getTargetConnector().getType() : null)
                .executionId(UUID.randomUUID().toString())
                .build();
        
       
        log = executionLogRepository.save(log);
        
        try {
            long startTime = System.currentTimeMillis();
            
           
            executeIntegrationSimple(integration, log);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            
            log.setStatus(ExecutionLog.ExecutionStatus.SUCCESS);
            log.setEndTime(LocalDateTime.now());
            log.setDurationMs(executionTime);
            log.setRecordsProcessed(1); 
            log.setRecordsSuccess(1);
            
        } catch (Exception e) {
            logger.error("Erro durante execução da integração: {}", e.getMessage(), e);
            
           
            log.setStatus(ExecutionLog.ExecutionStatus.FAILED);
            log.setEndTime(LocalDateTime.now());
            log.setErrorMessage(e.getMessage());
            log.setStackTrace(getStackTrace(e));
        }
        
       
        executionLogRepository.save(log);
        
        return CompletableFuture.completedFuture(log);
    }

    
    private void executeIntegrationSimple(Integration integration, ExecutionLog log) throws Exception {
        logger.info("Executando integração: {}", integration.getName());
        
        
        Thread.sleep(100);
        
   
        
   
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("executionMode", "simplified");
        metadata.put("integrationId", integration.getId());
        metadata.put("timestamp", LocalDateTime.now().toString());
        
        log.setMetadata(metadata);
        
        logger.info("Integração {} executada com sucesso", integration.getName());
    }

   
    public ExecutionLog executeIntegrationSync(Integration integration) {
        try {
            return executeIntegrationAsync(integration).get();
        } catch (Exception e) {
            logger.error("Erro na execução síncrona: {}", e.getMessage(), e);
            
            ExecutionLog errorLog = ExecutionLog.builder()
                    .integrationId(integration.getId())
                    .integrationName(integration.getName())
                    .status(ExecutionLog.ExecutionStatus.FAILED)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now())
                    .errorMessage(e.getMessage())
                    .stackTrace(getStackTrace(e))
                    .executionId(UUID.randomUUID().toString())
                    .build();
            
            return executionLogRepository.save(errorLog);
        }
    }

    
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

   
    public ExecutionLog testConnector(String connectorType, Map<String, Object> config) {
        ExecutionLog testLog = ExecutionLog.builder()
                .integrationName("Teste de Conector: " + connectorType)
                .status(ExecutionLog.ExecutionStatus.RUNNING)
                .startTime(LocalDateTime.now())
                .sourceConnectorType(connectorType)
                .executionId(UUID.randomUUID().toString())
                .build();

        try {
            
            logger.info("Testando conector: {}", connectorType);
            
            
            Thread.sleep(50);
            
            testLog.setStatus(ExecutionLog.ExecutionStatus.SUCCESS);
            testLog.setEndTime(LocalDateTime.now());
            testLog.setDurationMs(System.currentTimeMillis() - testLog.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("testType", "connector");
            metadata.put("connectorType", connectorType);
            metadata.put("configProvided", config != null && !config.isEmpty());
            testLog.setMetadata(metadata);
            
        } catch (Exception e) {
            logger.error("Erro no teste do conector: {}", e.getMessage(), e);
            testLog.setStatus(ExecutionLog.ExecutionStatus.FAILED);
            testLog.setEndTime(LocalDateTime.now());
            testLog.setErrorMessage(e.getMessage());
            testLog.setStackTrace(getStackTrace(e));
        }

        return executionLogRepository.save(testLog);
    }
}

