
package com.totvs.integration.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public boolean isAllowed(String tenantId, int maxRequestsPerMinute) {
        String key = "rate_limit:" + tenantId + ":" + getCurrentMinute();
        
        try {
            Long currentCount = redisTemplate.opsForValue().increment(key);
            
            if (currentCount == 1) {
                // Set expiration for the first request
                redisTemplate.expire(key, Duration.ofMinutes(1));
            }
            
            boolean allowed = currentCount <= maxRequestsPerMinute;
            
            if (!allowed) {
                log.warn("Rate limit exceeded for tenant: {}. Current: {}, Max: {}", 
                        tenantId, currentCount, maxRequestsPerMinute);
            }
            
            return allowed;
        } catch (Exception e) {
            log.error("Error checking rate limit for tenant: {}", tenantId, e);
            
            return true;
        }
    }
    
    public long getCurrentUsage(String tenantId) {
        String key = "rate_limit:" + tenantId + ":" + getCurrentMinute();
        
        try {
            Object count = redisTemplate.opsForValue().get(key);
            return count != null ? Long.parseLong(count.toString()) : 0;
        } catch (Exception e) {
            log.error("Error getting rate limit usage for tenant: {}", tenantId, e);
            return 0;
        }
    }
    
    public long getRemainingRequests(String tenantId, int maxRequestsPerMinute) {
        long currentUsage = getCurrentUsage(tenantId);
        return Math.max(0, maxRequestsPerMinute - currentUsage);
    }
    
    private long getCurrentMinute() {
        return System.currentTimeMillis() / (60 * 1000);
    }
}