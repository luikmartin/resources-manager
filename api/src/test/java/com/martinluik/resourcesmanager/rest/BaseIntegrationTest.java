package com.martinluik.resourcesmanager.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martinluik.resourcesmanager.config.TestContainersConfig;
import com.martinluik.resourcesmanager.repository.CharacteristicRepository;
import com.martinluik.resourcesmanager.repository.LocationRepository;
import com.martinluik.resourcesmanager.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Testcontainers
@ActiveProfiles("test")
@Tag("integration")
@Transactional
public abstract class BaseIntegrationTest {

  static final KafkaContainer KAFKA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"))
          .withReuse(true); // Enable container reuse for local dev
  @Autowired protected WebApplicationContext webApplicationContext;
  @Autowired protected CharacteristicRepository characteristicRepository;
  @Autowired protected ResourceRepository resourceRepository;
  @Autowired protected LocationRepository locationRepository;
  @Autowired protected ObjectMapper objectMapper;

  protected MockMvc mockMvc;

  @BeforeAll
  static void startKafka() {
    KAFKA.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    PostgreSQLContainer<?> postgres = TestContainersConfig.getPostgresContainer();
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    // Set Kafka bootstrap servers to the Testcontainers broker
    registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    registry.add("spring.kafka.admin.auto-create", () -> true);

    // Configure HikariCP for better test performance and reliability
    registry.add("spring.datasource.hikari.maximum-pool-size", () -> 2);
    registry.add("spring.datasource.hikari.minimum-idle", () -> 0);
    registry.add("spring.datasource.hikari.connection-timeout", () -> 10000);
    registry.add("spring.datasource.hikari.max-lifetime", () -> 120000);
    registry.add("spring.datasource.hikari.idle-timeout", () -> 60000);
    registry.add("spring.datasource.hikari.leak-detection-threshold", () -> 30000);
    registry.add("spring.datasource.hikari.validation-timeout", () -> 5000);
    registry.add("spring.datasource.hikari.keepalive-time", () -> 30000);
  }

  @BeforeEach
  void baseSetUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    characteristicRepository.deleteAll();
    resourceRepository.deleteAll();
    locationRepository.deleteAll();
  }
}
