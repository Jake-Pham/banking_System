package com.example.Banking_system;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Simple Bank Application",
				description = "Backend Rest API for the Bank Application",
				version = "v1.0",
				contact = @Contact(
						name = "Pham Trung Kien",
						email = "kienpham080101@gmail.com",
						url = "https://github.com/Jake-Pham/banking_System.git"
				),
				license = @License(
						name = "Jake-Pham",
						url = "https://github.com/Jake-Pham/banking_System.git"
				)

		),
		externalDocs = @ExternalDocumentation(
				description = "Backend Rest API for the Bank Application Documentation",
				url = "https://github.com/Jake-Pham/banking_System.git"
		)
)
public class BankingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingSystemApplication.class, args);
	}

}
//http://localhost:8080/swagger-ui/index.html#/