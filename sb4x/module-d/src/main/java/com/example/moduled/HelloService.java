package com.example.moduled;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

	@WithSpan("get-hello-message")
	public String getHelloMessage() {
		return "hello world";
	}
}
