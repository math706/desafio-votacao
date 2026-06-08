package com.votacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${votacao.base-url:http://localhost:8080}")
    private String baseUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Votação Cooperativa")
                .description("API REST para gerenciamento de sessões de votação em assembleias cooperativas.")
                .version("1.0.0"))
            .servers(List.of(
                new Server().url(baseUrl).description("Servidor principal")
            ));
    }
}
