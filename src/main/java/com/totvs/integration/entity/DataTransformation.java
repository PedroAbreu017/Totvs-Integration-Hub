package com.totvs.integration.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataTransformation {
    
  
    private Map<String, String> fieldMappings;
    
   
    private Map<String, String> typeConversions;
    
  
    private Map<String, Object> defaultValues;
    
   
    private List<ConditionalRule> conditionalRules;
    
    
    private String customScript;
    private String scriptLanguage;
    
   
    private String filterExpression;
    
   
    private List<AggregationRule> aggregationRules;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConditionalRule {
        private String condition; 
        private String action;
        private Map<String, Object> actionParameters;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AggregationRule {
        private String groupByField;
        private String aggregateField;
        private AggregationType type; 
        private String targetField;
    }
    
    public enum AggregationType {
        SUM, COUNT, AVG, MIN, MAX, FIRST, LAST
    }
}