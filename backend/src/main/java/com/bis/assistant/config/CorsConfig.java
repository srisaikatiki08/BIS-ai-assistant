package com.bis.assistant.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    public static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "https://bis-ai-assistant-nine.vercel.app",
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "http://localhost:5174",
            "http://127.0.0.1:5174",
            "http://localhost:5175",
            "http://127.0.0.1:5175",
            "http://localhost:3000",
            "http://127.0.0.1:3000"
    );

    public static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
    );

    public static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Content-Type", "Authorization", "Accept", "Origin", "X-Requested-With",
            "Access-Control-Request-Method", "Access-Control-Request-Headers"
    );

    public static final List<String> EXPOSED_HEADERS = Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Authorization"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGINS.toArray(new String[0]))
                .allowedMethods(ALLOWED_METHODS.toArray(new String[0]))
                .allowedHeaders(ALLOWED_HEADERS.toArray(new String[0]))
                .exposedHeaders(EXPOSED_HEADERS.toArray(new String[0]))
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        for (String origin : ALLOWED_ORIGINS) {
            config.addAllowedOrigin(origin);
        }
        for (String header : ALLOWED_HEADERS) {
            config.addAllowedHeader(header);
        }
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setExposedHeaders(EXPOSED_HEADERS);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
