package com.xpg.bookstore.nginxdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class NginxDemoApplication {
	@Value("${server.port}")
	private String port;

	public static void main(String[] args) {
		SpringApplication.run(NginxDemoApplication.class, args);
	}

	@GetMapping("/test")
	public String test() {
		return "Response from instance of NginxDemoApplication running on port: " + port;
	}
}
