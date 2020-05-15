package org.aashish.pointme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan
@EnableJpaAuditing
public class PointMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PointMeApplication.class, args);
	}

}
