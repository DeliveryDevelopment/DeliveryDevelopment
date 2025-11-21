package com.laioffer.delivery.geo;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoConfig {

    // here we use the key inside application.yml
    // not yet considering running the backend in cloud
    @Bean
    public GeoApiContext geoApiContext(@Value("${google.maps.api-key}") String myKey) {

        return new GeoApiContext
                .Builder()
                .apiKey(myKey)
                .build();
    }



}
