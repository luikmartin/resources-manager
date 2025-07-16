package com.martinluik.resourcesmanager.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainersConfig {

  private static PostgreSQLContainer<?> postgres;

  static {
    Runtime.getRuntime().addShutdownHook(new Thread(TestContainersConfig::stopContainer));
  }

  public static synchronized PostgreSQLContainer<?> getPostgresContainer() {
    if (postgres == null) {
      postgres =
          new PostgreSQLContainer<>("postgres:17-alpine")
              .withDatabaseName("testdb")
              .withUsername("test")
              .withPassword("test")
              .withReuse(true);
      postgres.start();
    }
    return postgres;
  }

  public static synchronized void stopContainer() {
    if (postgres != null && postgres.isRunning()) {
      postgres.stop();
      postgres = null;
    }
  }
}
