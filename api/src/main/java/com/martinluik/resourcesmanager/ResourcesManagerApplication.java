package com.martinluik.resourcesmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "com.martinluik.resourcesmanager.core.repository")
@EntityScan(basePackages = "com.martinluik.resourcesmanager.core.domain")
public class ResourcesManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ResourcesManagerApplication.class, args);
  }
}
