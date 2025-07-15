package com.martinluik.resourcesmanager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
public class StartupConfig {

    @Value("${server.port:8061}")
    private String serverPort;

    @Value("${server.servlet.context-path:/resources-manager}")
    private String contextPath;

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationUrls() {
        String baseUrl = "http://localhost:" + serverPort + contextPath;
        
        log.info("🚀 Resources Manager Application is ready!");
        log.info("📋 Access URLs:");
        log.info("   🌐 API Base URL: {}", baseUrl);
        log.info("   📖 Swagger UI: {}/swagger-ui.html", baseUrl);
        log.info("   📄 OpenAPI JSON: {}/v3/api-docs", baseUrl);
        log.info("   📄 OpenAPI YAML: {}/v3/api-docs.yaml", baseUrl);
        log.info("   ❤️  Health Check: {}/actuator/health", baseUrl);
        log.info("   🗄️  Database: localhost:5432");
        log.info("   📊 Kafka UI: http://localhost:8062");
        log.info("   📊 Kafka: localhost:9092");
    }
} 