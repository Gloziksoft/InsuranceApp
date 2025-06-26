package com.poistenie.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootTest
class PoistenieAppApplicationTests {

	@Test
	void contextLoads() {
	}

	@GetMapping("/test")
	@ResponseBody
	public String test() {
		return "Hello world!";
	}


}
