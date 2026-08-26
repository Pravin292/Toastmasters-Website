package com.rathinam.toastmasters.modules.ai.provider;

public interface AiProvider {

    /**
     * Generates text output for a given prompt using the underlying AI model.
     *
     * @param prompt the text prompt sent to the AI engine
     * @return generated text response from the AI engine
     */
    String generate(String prompt);

    /**
     * Returns the name of the AI provider (e.g. "gemini", "ollama", "openai").
     */
    String getProviderName();

    /**
     * Returns the model identifier used for generation (e.g. "gemini-1.5-flash").
     */
    String getModelName();
}
