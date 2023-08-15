package com.rafael.financebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinanceBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceBotApplication.class, args);
	}

}
