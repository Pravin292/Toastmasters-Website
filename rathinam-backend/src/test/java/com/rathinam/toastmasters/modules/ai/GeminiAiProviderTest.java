package com.rathinam.toastmasters.modules.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rathinam.toastmasters.modules.ai.exception.AiConfigurationException;
import com.rathinam.toastmasters.modules.ai.exception.AiQuotaExceededException;
import com.rathinam.toastmasters.modules.ai.exception.AiServiceUnavailableException;
import com.rathinam.toastmasters.modules.ai.provider.GeminiAiProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiAiProviderTest {

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void generate_Success() {
        GeminiAiProvider provider = new GeminiAiProvider("valid-key", "gemini-1.5-flash", 5, restClientBuilder, objectMapper);

        String jsonResponseBody = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          { "text": "Meeting #101 was a great success with 90% attendance." }
                        ]
                      }
                    }
                  ]
                }
                """;

        mockServer.expect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponseBody, MediaType.APPLICATION_JSON));

        String result = provider.generate("Summarize meeting");

        assertThat(result).isEqualTo("Meeting #101 was a great success with 90% attendance.");
        assertThat(provider.getProviderName()).isEqualTo("gemini");
        assertThat(provider.getModelName()).isEqualTo("gemini-1.5-flash");
    }

    @Test
    void generate_MissingApiKey_ThrowsAiConfigurationException() {
        GeminiAiProvider provider = new GeminiAiProvider("", "gemini-1.5-flash", 5, restClientBuilder, objectMapper);

        assertThatThrownBy(() -> provider.generate("Test prompt"))
                .isInstanceOf(AiConfigurationException.class)
                .hasMessageContaining("missing or not configured");
    }

    @Test
    void generate_QuotaExceeded_ThrowsAiQuotaExceededException() {
        GeminiAiProvider provider = new GeminiAiProvider("valid-key", "gemini-1.5-flash", 5, restClientBuilder, objectMapper);

        mockServer.expect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThatThrownBy(() -> provider.generate("Test prompt"))
                .isInstanceOf(AiQuotaExceededException.class);
    }

    @Test
    void generate_ServerError_ThrowsAiServiceUnavailableException() {
        GeminiAiProvider provider = new GeminiAiProvider("valid-key", "gemini-1.5-flash", 5, restClientBuilder, objectMapper);

        mockServer.expect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> provider.generate("Test prompt"))
                .isInstanceOf(AiServiceUnavailableException.class);
    }
}
