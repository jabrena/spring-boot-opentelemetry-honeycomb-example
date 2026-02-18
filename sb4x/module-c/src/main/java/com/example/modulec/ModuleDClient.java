package com.example.modulec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ModuleDClient {

	private final RestClient restClient;

	public ModuleDClient(RestClient.Builder restClientBuilder,
			@Value("${module-d.url}") String moduleDUrl) {
		this.restClient = restClientBuilder.baseUrl(moduleDUrl).build();
	}

	public String fetchHello() {
		return restClient.get().uri("/hello").retrieve().body(String.class);
	}
}
