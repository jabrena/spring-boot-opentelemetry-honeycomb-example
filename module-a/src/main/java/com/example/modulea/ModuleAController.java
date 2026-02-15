package com.example.modulea;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class ModuleAController {

	private final RestClient restClient;

	public ModuleAController(RestClient.Builder restClientBuilder,
			@Value("${module-b.url}") String moduleBUrl) {
		this.restClient = restClientBuilder.baseUrl(moduleBUrl).build();
	}

	@GetMapping("/")
	public String callModuleB() {
		return restClient.get().uri("/hello").retrieve().body(String.class);
	}
}
