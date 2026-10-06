package com.fiap.autoescola.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao do Swagger/OpenAPI 3 via springdoc.
 * UI disponivel em /swagger-ui.html e spec em /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoEscolaOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("AutoEscola API - Agendamento de Instrucoes")
                .description("""
                        API ReST desenvolvida com Spring Boot para gestao de uma auto-escola:
                        CRUD de Instrutores, Alunos, Usuarios e instrucoes (aulas praticas),
                        servico de agendamento/cancelamento com regras de negocio,
                        integracao com a API externa ViaCEP e documentacao automatica com Swagger.

                        Disciplina: SOA e WebServices - FIAP | Professor: Carlos Eduardo Machado de Oliveira
                        """)
                .version("1.0.0")
                .contact(new Contact().name("Grupo Checkpoint 5").email("pf2308@fiap.com.br"))
                .license(new License().name("MIT")));
    }
}
