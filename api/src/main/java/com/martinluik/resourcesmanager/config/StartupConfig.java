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

  @Value("${external.services.database.port:5432}")
  private String dbPort;

  @Value("${external.services.kafka.port:9092}")
  private String kafkaPort;

  @Value("${external.services.kafka-ui.port:8062}")
  private String kafkaUiPort;

  @EventListener(ApplicationReadyEvent.class)
  public void logApplicationUrls() {
    var baseUrl = "http://localhost:" + serverPort + contextPath;

    log.info("\n\n🚀 Resources Manager Application is ready!\n");
    log.info("📋 Access URLs:");
    log.info("   🌐 API Base URL: {}", baseUrl);
    log.info("   📖 Swagger UI: {}/swagger-ui.html", baseUrl);
    log.info("   📄 OpenAPI JSON: {}/v3/api-docs", baseUrl);
    log.info("   📄 OpenAPI YAML: {}/v3/api-docs.yaml", baseUrl);
    log.info("   ❤️ Health Check: {}/actuator/health", baseUrl);
    log.info("   🗄️ Database: http://localhost:{}", dbPort);
    log.info("   📊 Kafka: http://localhost:{}", kafkaPort);
    log.info("   📊 Kafka UI: http://localhost:{}\n", kafkaUiPort);
  }
}
