package com.mycode.datageniehack.datageniehack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@SpringBootApplication
public class DatageniehackApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatageniehackApplication.class, args);
	}
	@Bean
	public org.springframework.web.filter.CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// Allow requests from a specific origin
		config.setAllowedOrigins(List.of("http://localhost:5173")); // Add your frontend URL

		// Allow specific methods (GET, POST, etc.) and headers
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}


}
