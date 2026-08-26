package com.rathinam.toastmasters.modules.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.modules.ai.exception.AiConfigurationException;
import com.rathinam.toastmasters.modules.ai.exception.AiQuotaExceededException;
import com.rathinam.toastmasters.modules.ai.exception.AiServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiAiProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiProvider.class);
    private static final String GEMINI_API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private final String apiKey;
    private final String model;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiAiProvider(
            @Value("${ai.gemini.api-key:}") String apiKey,
            @Value("${ai.gemini.model:gemini-1.5-flash}") String model,
            @Value("${ai.gemini.timeout-seconds:10}") int timeoutSeconds,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null && !model.isBlank() ? model.trim() : "gemini-1.5-flash";
        this.objectMapper = objectMapper;

        this.restClient = restClientBuilder.build();
    }

    @Override
    public String generate(String prompt) {
        if (apiKey.isEmpty()) {
            log.warn("Gemini API request skipped: GEMINI_API_KEY is not configured.");
            throw new AiConfigurationException("AI functionality is disabled: Gemini API key is missing or not configured.");
        }

        String targetUrl = String.format(GEMINI_API_URL_TEMPLATE, model, apiKey);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            String rawResponse = restClient.post()
                    .uri(targetUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseGeminiResponse(rawResponse);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 429) {
                log.error("Gemini API rate limit or quota exceeded.");
                throw new AiQuotaExceededException("AI quota or rate limit exceeded. Please try again later.");
            }
            log.error("Gemini API HTTP client error status={}", ex.getStatusCode());
            throw new AiConfigurationException("AI generation failed due to invalid API configuration or request format.");
        } catch (HttpServerErrorException ex) {
            log.error("Gemini API server error status={}", ex.getStatusCode());
            throw new AiServiceUnavailableException("Gemini AI service is currently unavailable. Please try again later.", ex);
        } catch (Exception ex) {
            if (ex instanceof AiQuotaExceededException || ex instanceof AiConfigurationException || ex instanceof AiServiceUnavailableException) {
                throw ex;
            }
            log.error("Unexpected error during Gemini API text generation: {}", sanitizeMessage(ex.getMessage()));
            throw new AiServiceUnavailableException("Failed to generate AI content: " + sanitizeMessage(ex.getMessage()), ex);
        }
    }

    private String sanitizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return "";
        }
        if (apiKey != null && !apiKey.isBlank()) {
            return message.replace(apiKey, "[REDACTED_API_KEY]");
        }
        return message;
    }


    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public String getModelName() {
        return model;
    }

    private String parseGeminiResponse(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isBlank()) {
            throw new AiServiceUnavailableException("Received empty response from Gemini API.");
        }

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode parts = firstCandidate.path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    JsonNode firstPart = parts.get(0);
                    return firstPart.path("text").asText("");
                }
            }
            throw new AiServiceUnavailableException("Malformed or unparseable Gemini API response structure.");
        } catch (Exception e) {
            if (e instanceof AiServiceUnavailableException) {
                throw (AiServiceUnavailableException) e;
            }
            throw new AiServiceUnavailableException("Failed to parse Gemini API JSON response", e);
        }
    }
}
