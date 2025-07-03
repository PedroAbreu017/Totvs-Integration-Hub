package com.totvs.integration.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleConfig {
    
    private Boolean enabled;
    private ScheduleType scheduleType;
    
    
    private String cronExpression;
    
    
    private Integer intervalMinutes;
    private Integer intervalHours;
    private Integer intervalDays;
    
   
    private LocalTime dailyTime;
    private List<Integer> weekDays; 
    
   
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
   
    private Integer maxExecutions;
    private Integer currentExecutions;
    
   
    private Boolean retryOnFailure;
    private Integer maxRetries;
    private Integer retryDelayMinutes;
    
    public enum ScheduleType {
        MANUAL, INTERVAL, DAILY, WEEKLY, MONTHLY, CRON
    }
}