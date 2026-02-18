package com.example.modulec;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModuleCController {

	private final HelloAggregatorService helloAggregatorService;

	public ModuleCController(HelloAggregatorService helloAggregatorService) {
		this.helloAggregatorService = helloAggregatorService;
	}

	@GetMapping("/")
	public String callModuleD() {
		return helloAggregatorService.aggregateHello();
	}
}
