package com.bancolombia.clientsdinner;

import com.bancolombia.clientsdinner.service.ClientsDinnerServiceI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientsdinnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientsdinnerApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ClientsDinnerServiceI dinnerService) {
		return (args) -> {
			System.out.println(dinnerService.makeTableFilters());
		};
	}

}
