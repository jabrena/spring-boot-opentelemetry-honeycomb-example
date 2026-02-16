package com.example.modulea;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModuleAController {

	private final HelloAggregatorService helloAggregatorService;

	public ModuleAController(HelloAggregatorService helloAggregatorService) {
		this.helloAggregatorService = helloAggregatorService;
	}

	@GetMapping("/")
	public String callModuleB() {
		return helloAggregatorService.aggregateHello();
	}
}
