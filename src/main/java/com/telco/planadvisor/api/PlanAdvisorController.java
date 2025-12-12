package com.telco.planadvisor.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telco.planadvisor.service.GroqService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // for later when you have a frontend
public class PlanAdvisorController {

    private final GroqService groqService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlanAdvisorController(GroqService groqService) {
        this.groqService = groqService;
    }

    @PostMapping("/compare-plans")
    public ResponseEntity<PlanComparisonResponse> comparePlans(
            @Valid @RequestBody PlanComparisonRequest request
    ) {
        // For now: ignore the actual text, just return a fake response
        PlanComparisonResponse response = new PlanComparisonResponse();

        List<PlanComparisonResponse.PlanSummary> plans = new ArrayList<>();

        PlanComparisonResponse.PlanSummary p1 = new PlanComparisonResponse.PlanSummary();
        p1.setCarrier("Fido");
        p1.setName("Fido 20GB 5G");
        p1.setPrice("$55 / month");
        p1.setData("20GB");
        p1.setNotes("Good for moderate users, no roaming");
        plans.add(p1);

        PlanComparisonResponse.PlanSummary p2 = new PlanComparisonResponse.PlanSummary();
        p2.setCarrier("Koodo");
        p2.setName("Koodo 25GB 5G");
        p2.setPrice("$60 / month");
        p2.setData("25GB");
        p2.setNotes("Slightly more data, close to your budget");
        plans.add(p2);

        response.setParsedPlans(plans);
        response.setRecommendedCarrier("Koodo");
        response.setRecommendedPlanName("Koodo 25GB 5G");
        response.setReasoning(
                "Dummy response: chosen because it fits within your budget and offers a bit more data."
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-groq")
    public Mono<String> testGroq() {
        return groqService.askGroq("Say hello in a funny Canadian slang.");
    }

    @PostMapping("/ai/summarize")
    public Mono<String> summarize(@RequestBody Map<String, String> body) {
        String text = body.get("text");

        if (text == null || text.isBlank()) {
            return Mono.just("No text provided.");
        }

        return groqService.askGroq(
                "You are a telecom expert. Summarize the following phone plan clearly and concisely. "
                        + "Avoid jokes. Keep it factual.\n\nPlan text:\n" + text
        );
    }

    @PostMapping("/ai/extract-plan")
    public Mono<ExtractedPlan> extractPlan(@RequestBody Map<String, String> body) {
        String text = body.get("text");

        if (text == null || text.isBlank()) {
            return Mono.error(new IllegalArgumentException("Missing plan text."));
        }

        String prompt = """
            Extract the following mobile plan into STRICT JSON with fields:
            carrier, name, price, data, extras.
            
            Rules:
            - price MUST be a string (like "$75/month")
            - extras MUST be a comma-separated string (not an array)
            - Do NOT include explanations
            - RETURN ONLY JSON
            
            Plan text:
            """ + text;



        return groqService.askGroq(prompt)
                .map(raw -> {
                    try {
                        String cleaned = raw
                                .replaceAll("```json", "")
                                .replaceAll("```", "")
                                .trim();

                        return new com.fasterxml.jackson.databind.ObjectMapper()
                                .readValue(cleaned, ExtractedPlan.class);

                    } catch (Exception e) {
                        throw new RuntimeException("AI returned invalid JSON: " + raw);
                    }
                });

    }

    @PostMapping("/ai/extract-plans")
    public Mono<List<ExtractedPlan>> extractPlans(@RequestBody Map<String, String> body) {
        String text = body.get("text");

        if (text == null || text.isBlank()) {
            return Mono.error(new IllegalArgumentException("Missing plan text"));
        }

        String prompt = """
            You are a telecom expert.
            Extract ALL mobile plans from the text below.

            RETURN STRICT JSON ONLY.
            The response MUST be a JSON ARRAY.

            Each object must have:
            - carrier
            - name
            - price (string, e.g. "$75/month")
            - data
            - extras (comma-separated string)

            DO NOT explain.
            DO NOT use markdown.
            DO NOT add commentary.

            TEXT:
            """ + text;

        return groqService.askGroq(prompt)
                .map(this::sanitizeJson)
                .map(json -> {
                    try {
                        return objectMapper.readValue(
                                json,
                                new TypeReference<List<ExtractedPlan>>() {}
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("AI returned invalid JSON:\n" + json);
                    }
                });
    }

    /* -------------------------------
       JSON SANITIZER (CRITICAL)
       ------------------------------- */

    private String sanitizeJson(String raw) {
        String cleaned = raw.trim();

        // Remove markdown fences if AI adds them
        if (cleaned.startsWith("```")) {
            cleaned = cleaned
                    .replaceAll("^```json", "")
                    .replaceAll("^```", "")
                    .replaceAll("```$", "")
                    .trim();
        }

        // Fix accidental double closing brackets
        while (cleaned.endsWith("]]")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }
}
