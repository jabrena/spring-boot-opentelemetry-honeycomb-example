package com.example.moduleb;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/hello")
	@WithSpan("generate-hello-response")
	public String hello() {
		return "hello world";
	}
}
