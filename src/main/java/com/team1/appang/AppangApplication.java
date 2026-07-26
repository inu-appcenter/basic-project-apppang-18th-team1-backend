package com.team1.appang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AppangApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppangApplication.class, args);
	}

}
