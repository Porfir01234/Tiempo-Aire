package com.tiempoaire.consultasaldo.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration // Indica a Spring que esta clase define Beans
public class JacksonConfig {

    @Bean // Registra el ObjectMapper en el ApplicationContext
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}