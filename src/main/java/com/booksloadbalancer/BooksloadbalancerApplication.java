package com.booksloadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class BooksloadbalancerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooksloadbalancerApplication.class, args);
	}

	
	@Bean
	public RestTemplate resttemplate() {
		return new RestTemplate();
	}
}
