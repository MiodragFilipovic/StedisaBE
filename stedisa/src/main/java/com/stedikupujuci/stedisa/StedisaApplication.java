package com.stedikupujuci.stedisa;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class StedisaApplication {

	public static void main(String[] args) {
		SpringApplication.run(StedisaApplication.class, args);
	}

}
