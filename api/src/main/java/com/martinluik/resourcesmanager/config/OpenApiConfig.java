package com.martinluik.resourcesmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resources Manager API")
                        .description("A comprehensive API for managing resources, locations, and characteristics")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Martin Luik")
                                .email("martin.luik@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8061/resources-manager")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://your-production-domain.com/resources-manager")
                                .description("Production Server")
                ));
    }
} 