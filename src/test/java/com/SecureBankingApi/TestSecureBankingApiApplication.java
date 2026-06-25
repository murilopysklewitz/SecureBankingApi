package com.SecureBankingApi;

import org.springframework.boot.SpringApplication;

public class TestSecureBankingApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SecureBankingApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
