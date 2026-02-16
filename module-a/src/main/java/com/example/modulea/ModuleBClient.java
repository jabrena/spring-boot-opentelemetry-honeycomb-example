package com.example.modulea;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ModuleBClient {

	private final RestClient restClient;

	public ModuleBClient(RestClient.Builder restClientBuilder,
			@Value("${module-b.url}") String moduleBUrl) {
		this.restClient = restClientBuilder.baseUrl(moduleBUrl).build();
	}

	//@WithSpan("fetch-module-b-hello")
	public String fetchHello() {
		return restClient.get().uri("/hello").retrieve().body(String.class);
	}
}
