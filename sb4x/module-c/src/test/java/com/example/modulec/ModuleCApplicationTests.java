package com.example.modulec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "module-d.url=http://localhost:8083")
class ModuleCApplicationTests {

	@Test
	void contextLoads() {
	}
}
