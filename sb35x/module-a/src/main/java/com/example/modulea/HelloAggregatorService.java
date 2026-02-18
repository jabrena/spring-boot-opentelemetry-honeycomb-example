package com.example.modulea;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.stereotype.Service;

@Service
public class HelloAggregatorService {

	private final ModuleBClient moduleBClient;

	public HelloAggregatorService(ModuleBClient moduleBClient) {
		this.moduleBClient = moduleBClient;
	}

	@WithSpan("aggregate-hello-from-module-b")
	public String aggregateHello() {
		return moduleBClient.fetchHello();
	}
}
