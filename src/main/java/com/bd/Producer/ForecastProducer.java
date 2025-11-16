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
import java.util.logging.Logger;

@Component
public class ForecastProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final WebClient webClient;
    private Logger logger;

    public ForecastProducer(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services").build();
    }

    private String getWeatherData() {
        System.out.println("Fetching day forecast...");
        LocalDateTime currentDateAndTime = LocalDateTime.now();
        System.out.println(currentDateAndTime);
        String currentDate = currentDateAndTime.format(formatter);
//        String elements = "temp,feelslike";
        String locations = "London,UK|Paris,France|Tokyo,Japan|Cape Town,South Africa";
//        return new String(Files.readAllBytes(Paths.get("src/main/resources/mockData/forecastMock.json")));
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/timelinemulti")
                        .queryParam("key", Environments.weatherApi.getValue())
                        .queryParam("locations", locations)
                        .queryParam("datestart", currentDate)
                        .queryParam("include", "hours")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Scheduled(fixedRate = 6000)
    private void publish() throws IOException {
        String weatherData = getWeatherData();
        kafkaTemplate.send("weather_logs_forecast", weatherData);
    }
}