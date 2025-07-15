package com.martinluik.resourcesmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${server.port:8080}")
  private String serverPort;

  @Value("${server.servlet.context-path:/}")
  private String contextPath;

  @Bean
  public OpenAPI customOpenAPI() {
    var baseUrl = "http://localhost:" + serverPort + contextPath;

    return new OpenAPI()
        .info(
            new Info()
                .title("Resources Manager API")
                .description(
                    "A comprehensive API for managing resources, locations, and characteristics")
                .version("1.0.0")
                .contact(new Contact().name("Martin Luik").email("martin.luik@example.com")))
        .servers(List.of(new Server().url(baseUrl).description("Current Server")));
  }
}
