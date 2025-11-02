package br.com.jobinder.authservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info = @Info(
		title = "Jobinder - Authentication Service API",
		version = "1.0",
		description = "API for user authentication and JWT token generation"
))
public class AuthserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthserviceApplication.class, args);
	}
}
