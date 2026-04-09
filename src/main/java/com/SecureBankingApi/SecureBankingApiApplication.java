package com.SecureBankingApi;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class SecureBankingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureBankingApiApplication.class, args);
	}

}
