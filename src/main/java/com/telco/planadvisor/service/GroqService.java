package com.telco.planadvisor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final WebClient webClient;

    public GroqService(@Value("${groq.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<String> askGroq(String prompt) {
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(Map.of(
                        "model", "llama-3.1-8b-instant",
                        "messages", List.of(
                                Map.of("role", "user", "content", prompt)
                        ),
                        "temperature", 0.3
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> {
                    var choices = (List<?>) resp.get("choices");
                    var message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
                    return message.get("content").toString();
                });
    }
}

