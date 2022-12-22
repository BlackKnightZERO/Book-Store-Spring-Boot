package com.demo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.demo.beans, com.demo.controller, com.demo.database, com.demo.security")
public class Java3Assignment4Application {

	public static void main(String[] args) {
		SpringApplication.run(Java3Assignment4Application.class, args);
	}

}
