/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Main class for starting the REST service with Cross-Origin Resource Sharing (CORS) support.
 * The class configures CORS to allow requests only from specific origins for different endpoints.
 */

@SpringBootApplication
public class RestServiceCorsApplication {
	/** Allowed origins for production and debug environments */
    static String allowedOrigins = "http://tweetyproject.org";
	static String debug_allowedOrigins = "http://127.0.0.1:5500/";
    /**
     * Main method that starts the Spring Boot application and logs the allowed origins.
     * It also sets up necessary configurations and initializes the Spring context.
     *
     * @param args Command line arguments passed during the start of the application.
     */
	public static void main(String[] args) {
		LoggerUtil.logger.info("Server started.");
		LoggerUtil.logger.info("Allowed Origins: "+ allowedOrigins);

		SpringApplication.run(RestServiceCorsApplication.class, args);
	}
	/**
     * Configuration method for CORS support in the application.
     *
     * @return A WebMvcConfigurer instance that configures CORS settings.
     */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/delp").allowedOrigins(allowedOrigins);
				registry.addMapping("/ping").allowedOrigins(allowedOrigins);
				registry.addMapping("/incmes").allowedOrigins(allowedOrigins);
				registry.addMapping("/aba").allowedOrigins(allowedOrigins);
			}
		};
	}

}
