package com.aadeshandreas.ailearning.ai_learning_companion.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class that defines global CORS (Cross-Origin Resource Sharing) settings
 * for the application. This enables secure communication between the backend API and
 * the frontend.
 *
 * <p>Allows requests from {@code http://localhost:3000} (the default development frontend),
 * supports credentials, and permits common HTTP methods including GET, POST, PUT, and DELETE.</p>
 */
@Configuration
public class CorsConfig {

    /**
     * Creates and configures a {@link WebMvcConfigurer} bean that sets up CORS mappings.
     *
     * <p>This configuration:</p>
     * <ul>
     *   <li>Applies to all endpoints</li>
     *   <li>Allows requests from {@code http://localhost:3000}</li>
     *   <li>Permits methods: GET, POST, PUT, DELETE</li>
     *   <li>Accepts all headers</li>
     *   <li>Enables credential sharing (cookies, authorization headers, etc.)</li>
     * </ul>
     *
     * @return a configured {@link WebMvcConfigurer} instance with custom CORS rules
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
