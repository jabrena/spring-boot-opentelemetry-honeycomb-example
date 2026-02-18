package com.example.gateway;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI gatewayOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("API Gateway")
						.version("1.0")
						.description("Gateway routes to Module A (Spring Boot 3.5) or Module C (Spring Boot 4)"))
				.servers(List.of(
						new Server().url("/").description("Gateway")));
	}
}
