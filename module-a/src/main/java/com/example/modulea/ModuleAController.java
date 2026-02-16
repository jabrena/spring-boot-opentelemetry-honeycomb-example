package com.example.modulea;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class ModuleAController {

	private final RestClient restClient;
	private final Tracer tracer;

	public ModuleAController(RestClient.Builder restClientBuilder,
			@Value("${module-b.url}") String moduleBUrl,
			OpenTelemetry openTelemetry) {
		this.restClient = restClientBuilder.baseUrl(moduleBUrl).build();
		this.tracer = openTelemetry.getTracer("module-a", "1.0.0");
	}

	@GetMapping("/")
	public String callModuleB() {
		// Custom span: aggregate hello from module-b
		Span span = tracer.spanBuilder("aggregate-hello-from-module-b").startSpan();
		try (var scope = span.makeCurrent()) {
			span.setAttribute("upstream.service", "module-b");
			span.setAttribute("operation", "fetch-hello");

			// Custom span: HTTP call to module-b (child of aggregate span)
			Span fetchSpan = tracer.spanBuilder("fetch-module-b-hello").startSpan();
			String response;
			try (var fetchScope = fetchSpan.makeCurrent()) {
				fetchSpan.setAttribute("http.target", "/hello");
				response = restClient.get().uri("/hello").retrieve().body(String.class);
				fetchSpan.setAttribute("response.length", response.length());
			} catch (Exception e) {
				fetchSpan.recordException(e);
				fetchSpan.setStatus(StatusCode.ERROR, e.getMessage());
				throw e;
			} finally {
				fetchSpan.end();
			}

			span.setAttribute("response.preview", response.length() > 20 ? response.substring(0, 20) + "..." : response);
			return response;
		} catch (Exception e) {
			span.recordException(e);
			span.setStatus(StatusCode.ERROR, e.getMessage());
			throw e;
		} finally {
			span.end();
		}
	}
}
