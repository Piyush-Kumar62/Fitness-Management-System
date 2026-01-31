package com.project.fitness.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Fitness Management System API")
            .description("REST APIs for fitness tracking, recommendations, and user management")
            .version("v1.0")
            .contact(new Contact()
                .name("Piyush Kumar")
                .url("https://www.linkedin.com/in/piyush-kumar62/")
                .email("piyushkumar30066@gmail.com")
            )
            .license(new License()
                .name("Apache 2.0")
                .url("http://springdoc.org")
            )
        );
  }
}
