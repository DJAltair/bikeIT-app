package com.bikeit.restendpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.bikeit.restendpoint.*")
@ComponentScan(basePackages = { "com.bikeit.restendpoint.*" })
@EntityScan("com.bikeit.restendpoint.*")
public class RestEndpointApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestEndpointApplication.class, args);
	}

}
