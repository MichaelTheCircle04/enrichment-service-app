package com.mtrifonov.enrichmentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 *
 * @Mikhail Trifonov
 */
@SpringBootApplication
public class EnrichmentServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(EnrichmentServiceApp.class, args);
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
