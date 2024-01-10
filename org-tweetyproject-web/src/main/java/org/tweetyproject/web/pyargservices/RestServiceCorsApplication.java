package org.tweetyproject.web.pyargservices;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class RestServiceCorsApplication {
    String allowedOrigins = "http://tweetyproject.org";
	String debug_allowedOrigins = "http://127.0.0.1:5500/";
	public static void main(String[] args) {
		SpringApplication.run(RestServiceCorsApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/delp").allowedOrigins(allowedOrigins);
				registry.addMapping("/ping").allowedOrigins(allowedOrigins);
				registry.addMapping("/incmes").allowedOrigins(allowedOrigins);
				registry.addMapping("/aba").allowedOrigins(debug_allowedOrigins);
			}
		};
	}

}
