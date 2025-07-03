package com.totvs.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
@EnableCaching
public class IntegrationApplication {

    public static void main(String[] args) {
        
        System.out.println("🚀 INICIANDO TOTVS INTEGRATION HUB");
        System.out.println("📦 Spring Boot: " + SpringApplication.class.getPackage().getImplementationVersion());
        
        
        String mongoUri = System.getenv("SPRING_DATA_MONGODB_URI");
        System.out.println("🔧 MONGODB_URI: " + (mongoUri != null ? mongoUri : "NÃO DEFINIDA"));
        
        SpringApplication.run(IntegrationApplication.class, args);
    }
}