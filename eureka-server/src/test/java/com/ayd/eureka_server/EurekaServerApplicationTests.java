package com.ayd.eureka_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EurekaServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodRunsWithoutException() {
		EurekaServerApplication.main(new String[] {});
	}
}
