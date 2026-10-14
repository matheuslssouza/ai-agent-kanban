package com.kanban.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI kanbanOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kanban Board API - GitHub Projects Style")
                        .description("Clean Architecture Layered Monolith with Controller, Service, Repository interfaces. Ready for AI / MCP integration.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Antigravity & AI Assistant")
                                .url("http://localhost:8080"))
                        .license(new License().name("Apache 2.0")));
    }
}
