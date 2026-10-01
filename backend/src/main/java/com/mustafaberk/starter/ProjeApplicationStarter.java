package com.mustafaberk.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = {"com.mustafaberk"})
@EntityScan(basePackages = {"com.mustafaberk"})
@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.mustafaberk"})
public class ProjeApplicationStarter {

	public static void main(String[] args) {
		SpringApplication.run(ProjeApplicationStarter.class, args);
	}

}
