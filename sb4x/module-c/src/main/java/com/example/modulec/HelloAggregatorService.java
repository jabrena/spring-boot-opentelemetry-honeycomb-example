package com.example.modulec;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.stereotype.Service;

@Service
public class HelloAggregatorService {

	private final ModuleDClient moduleDClient;

	public HelloAggregatorService(ModuleDClient moduleDClient) {
		this.moduleDClient = moduleDClient;
	}

	@WithSpan("aggregate-hello-from-module-d")
	public String aggregateHello() {
		return moduleDClient.fetchHello();
	}
}
