package com.example.gateway;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI gatewayOpenAPI() {
		Schema<?> stringSchema = new Schema<Object>().type("string").example("hello world");

		return new OpenAPI()
				.info(new Info()
						.title("API Gateway")
						.version("1.0")
						.description("Gateway routes to Module A (Spring Boot 3.5) or Module C (Spring Boot 4)"))
				.servers(List.of(
						new Server().url("/").description("Gateway")))
				.path("/api/a",
						new io.swagger.v3.oas.models.PathItem()
								.get(new Operation()
										.operationId("proxyA")
										.summary("Call Module A")
										.description("Proxies to module-a (Spring Boot 3.5). Returns aggregated hello from module-a -> module-b.")
										.responses(new ApiResponses()
												.addApiResponse("200",
														new ApiResponse()
																.description("OK")
																.content(new Content()
																		.addMediaType("text/plain", new MediaType().schema(stringSchema)))))))
				.path("/api/c",
						new io.swagger.v3.oas.models.PathItem()
								.get(new Operation()
										.operationId("proxyC")
										.summary("Call Module C")
										.description("Proxies to module-c (Spring Boot 4). Returns aggregated hello from module-c -> module-d.")
										.responses(new ApiResponses()
												.addApiResponse("200",
														new ApiResponse()
																.description("OK")
																.content(new Content()
																		.addMediaType("text/plain", new MediaType().schema(stringSchema)))))));
	}
}
