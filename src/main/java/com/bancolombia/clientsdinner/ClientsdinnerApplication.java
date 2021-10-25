package com.bancolombia.clientsdinner;

import com.bancolombia.clientsdinner.entity.TableGroup;
import com.bancolombia.clientsdinner.service.ClientsDinnerServiceI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableFeignClients
public class ClientsdinnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientsdinnerApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ClientsDinnerServiceI dinnerService) {
		return (args) -> {
			dinnerService.makeTableFilters().forEach(table ->
					System.out.println(table.getTableName()+"\n"+
							table.getClientsCodes().stream().map(String::valueOf).collect(Collectors.joining(","))));
		};
	}

}
