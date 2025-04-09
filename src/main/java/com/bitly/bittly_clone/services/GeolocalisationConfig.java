package com.bitly.bittly_clone.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.ipgeolocation.api.IPGeolocationAPI;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GeolocalisationConfig {
    @Value("${geolocalisation.api.key}")
    private String apiKey;

    @Bean
    public IPGeolocationAPI ipGeolocationAPI() {
        return new IPGeolocationAPI(apiKey);
    }

}
