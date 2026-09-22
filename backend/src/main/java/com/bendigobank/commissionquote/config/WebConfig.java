package com.bendigobank.commissionquote.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the Vite dev server (http://localhost:5173) to call the platform
 * API directly during local development. The vendor mock endpoint is
 * intentionally NOT exposed here - only our own backend should ever talk
 * to it, since it holds the vendor api-key.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
}
