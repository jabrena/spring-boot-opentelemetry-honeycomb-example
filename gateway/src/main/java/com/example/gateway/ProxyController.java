package com.example.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping
@Tag(name = "API Gateway", description = "Routes /api/a to module-a (SB 3.5), /api/c to module-c (SB 4)")
public class ProxyController {

	private final RestClient restClientA;
	private final RestClient restClientC;

	public ProxyController(
			@Value("${module-a.uri:http://module-a:8080}") String moduleAUri,
			@Value("${module-c.uri:http://module-c:8080}") String moduleCUri,
			RestClient.Builder restClientBuilder) {
		this.restClientA = restClientBuilder.baseUrl(moduleAUri).build();
		this.restClientC = restClientBuilder.baseUrl(moduleCUri).build();
	}

	@GetMapping("/api/a")
	@Operation(summary = "Call Module A", description = "Proxies to module-a (Spring Boot 3.5). Returns aggregated hello from module-a -> module-b.")
	public ResponseEntity<String> proxyA() {
		String body = restClientA.get().uri("/").retrieve().body(String.class);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/api/a/**")
	@Operation(summary = "Call Module A (path)", description = "Proxies to module-a root path.")
	public ResponseEntity<String> proxyAPath() {
		String body = restClientA.get().uri("/").retrieve().body(String.class);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/api/c")
	@Operation(summary = "Call Module C", description = "Proxies to module-c (Spring Boot 4). Returns aggregated hello from module-c -> module-d.")
	public ResponseEntity<String> proxyC() {
		String body = restClientC.get().uri("/").retrieve().body(String.class);
		return ResponseEntity.ok(body);
	}

	@GetMapping("/api/c/**")
	@Operation(summary = "Call Module C (path)", description = "Proxies to module-c root path.")
	public ResponseEntity<String> proxyCPath() {
		String body = restClientC.get().uri("/").retrieve().body(String.class);
		return ResponseEntity.ok(body);
	}
}
