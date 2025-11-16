package com.bd.Producer;

import com.bd.Environments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class CurrentWeatherProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H:mm:ss");

    private final WebClient webClient;

    public CurrentWeatherProducer(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services").build();
    }


    private String getWeatherData() throws IOException {
        System.out.println("Fetching weather...");
        LocalDateTime currentDateAndTime = LocalDateTime.now();
        String currentDate = currentDateAndTime.format(formatter);
        String locations = "London,UK|Paris,France|Tokyo,Japan|Cape Town,South Africa";
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/timelinemulti")
                        .queryParam("key", Environments.weatherApi.getValue())
                        .queryParam("locations", locations)
                        .queryParam("datestart", currentDate)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Scheduled(fixedRate = 180000000)
    private void sendData() throws IOException {
        String message = getWeatherData();
        kafkaTemplate.send("weather-logs", message);
    }

}
