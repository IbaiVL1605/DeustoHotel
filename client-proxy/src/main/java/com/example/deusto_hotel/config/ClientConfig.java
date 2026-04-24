package com.example.deusto_hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ClientConfig {

    @Bean
    public HttpClient httpClient() {
        // Aquí puedes configurar timeouts globales si quieres
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
}