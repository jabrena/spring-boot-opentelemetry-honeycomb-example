package com.example.modulea;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "module-b.url=http://localhost:8081")
class ModuleAApplicationTests {

	@Test
	void contextLoads() {
	}
}
