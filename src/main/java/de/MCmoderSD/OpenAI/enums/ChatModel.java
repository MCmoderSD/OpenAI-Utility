package de.MCmoderSD.OpenAI.enums;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Enum representing various Chat Models and their associated properties.
 * Provides methods to calculate token usage costs and validate model-specific configurations.
 */
@SuppressWarnings("ALL")
public enum ChatModel {

    // Models
    GPT_4O("gpt-4o", 0.0025, 0.00125,0.01, 16384, 128000),
    GPT_4O_2024_11_20("gpt-4o-2024-11-20", 0.0025, 0.00125, 0.01, 16384, 128000),
    GPT_4O_2024_08_06("gpt-4o-2024-08-06", 0.0025, 0.01, 0.00125, 16384, 128000),
    GPT_4O_2024_05_13("gpt-4o-2024-05-13", 0.005, 0.015, 4096, 128000),
    CHATGPT_4O_LATEST("chatgpt-4o-latest", 0.005, 0.015, 16384, 128000),

    GPT_4O_MINI("gpt-4o-mini", 0.00015, 0.000075, 0.0006, 16384, 128000),
    GPT_4O_MINI_2024_07_18("gpt-4o-mini-2024-07-18", 0.00015, 0.000075, 0.0006, 16384, 128000),

    O1_PREVIEW("o1-preview", 0.015, 0.0075, 0.06, 32768, 128000),
    O1_PREVIEW_2024_09_12("o1-preview-2024-09-12", 0.015, 0.0075, 0.06, 32768, 128000),

    O1_MINI("o1-mini", 0.003, 0.012, 65536, 128000),
    O1_MINI_2024_09_12("o1-mini-2024-09-12", 0.003, 0.012, 65536, 128000);


    /**
     * Set of valid model names.
     */
    private final HashSet<String> models;

    /**
     * Minimum allowed temperature.
     */
    private final double minTemperature;

    /**
     * Maximum allowed temperature.
     */
    private final double maxTemperature;

    /**
     * Minimum allowed top-p value.
     */
    private final double minTopP;

    /**
     * Maximum allowed top-p value.
     */
    private final double maxTopP;

    /**
     * Minimum allowed frequency penalty.
     */
    private final double minFrequencyPenalty;

    /**
     * Maximum allowed frequency penalty.
     */
    private final double maxFrequencyPenalty;

    /**
     * Minimum allowed presence penalty.
     */
    private final double minPresencePenalty;

    /**
     * Maximum allowed presence penalty.
     */
    private final double maxPresencePenalty;

    /**
     * The model identifier.
     */
    private final String model;

    /**
     * Cost of input tokens in USD.
     */
    private final BigDecimal inputPrice;

    /**
     * Cost of cached input tokens in USD.
     */
    private final BigDecimal cachedInputPrice;

    /**
     * Cost of output tokens in USD.
     */
    private final BigDecimal outputPrice;

    /**
     * Maximum allowed output tokens.
     */
    private final int maxOutputTokens;

    /**
     * Context window size in tokens.
     */
    private final int contextWindow;

    /**
     * Constructs a ChatModel with the specified parameters.
     *
     * @param model           the model identifier
     * @param input           cost per input token
     * @param output          cost per output token
     * @param maxOutputTokens maximum allowed output tokens
     * @param contextWindow   context window size in tokens
     */
    ChatModel(String model, double input, double output, int maxOutputTokens, int contextWindow) {
        this(model, input, null, output, maxOutputTokens, contextWindow);
    }

    /**
     * Constructs a ChatModel with additional cached input token price.
     *
     * @param model           the model identifier
     * @param input           cost per input token
     * @param cached          cost per cached input token (nullable)
     * @param output          cost per output token
     * @param maxOutputTokens maximum allowed output tokens
     * @param contextWindow   context window size in tokens
     */
    ChatModel(String model, double input, @Nullable Double cached, double output, int maxOutputTokens, int contextWindow) {

        // Initialize attributes
        models = new HashSet<>();
        minTemperature = 0.0;
        maxTemperature = 2.0;
        minTopP = 0;
        maxTopP = 1;
        minFrequencyPenalty = 0;
        maxFrequencyPenalty = 2;
        minPresencePenalty = 0;
        maxPresencePenalty = 2;

        // Set models
        models.add("gpt-4o");
        models.add("gpt-4o-2024-11-20");
        models.add("gpt-4o-2024-08-06");
        models.add("gpt-4o-2024-05-13");
        models.add("chatgpt-4o-latest");

        models.add("gpt-4o-mini");
        models.add("gpt-4o-mini-2024-07-18");

        models.add("o1-preview");
        models.add("o1-preview-2024-09-12");

        models.add("o1-mini");
        models.add("o1-mini-2024-09-12");

        // Set Attributes
        if (models.contains(model)) this.model = model;
        else throw new IllegalArgumentException("Invalid model");
        inputPrice = new BigDecimal(input).movePointLeft(4);
        if (cached == null) cachedInputPrice = null;
        else cachedInputPrice = new BigDecimal(cached).movePointLeft(4);
        outputPrice = new BigDecimal(output).movePointLeft(4);
        this.maxOutputTokens = maxOutputTokens;
        this.contextWindow = contextWindow;
    }

    /**
     * Calculates the cost of token usage.
     *
     * @param inputTokens  number of input tokens
     * @param outputTokens number of output tokens
     * @return the total cost as {@link BigDecimal}
     */
    public BigDecimal calculateCost(int inputTokens, int outputTokens) {
        return inputPrice.multiply(new BigDecimal(inputTokens)).add(outputPrice.multiply(new BigDecimal(outputTokens)));
    }

    /**
     * Calculates the cost for the same number of input and output tokens.
     *
     * @param tokens number of tokens
     * @return the total cost as {@link BigDecimal}
     */
    public BigDecimal calculateCost(int tokens) {
        return inputPrice.multiply(new BigDecimal(tokens)).add(outputPrice.multiply(new BigDecimal(tokens)));
    }

    /**
     * Retrieves the set of all valid model identifiers.
     *
     * @return a {@link HashSet} containing valid model names
     */
    public HashSet<String> getModels() {
        return models;
    }

    /**
     * Retrieves the minimum allowed temperature.
     *
     * @return the minimum temperature as a {@code double}
     */
    public double getMinTemperature() {
        return minTemperature;
    }

    /**
     * Retrieves the maximum allowed temperature.
     *
     * @return the maximum temperature as a {@code double}
     */
    public double getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * Retrieves the minimum allowed top-p value.
     *
     * @return the minimum top-p as a {@code double}
     */
    public double getMinTopP() {
        return minTopP;
    }

    /**
     * Retrieves the maximum allowed top-p value.
     *
     * @return the maximum top-p as a {@code double}
     */
    public double getMaxTopP() {
        return maxTopP;
    }

    /**
     * Retrieves the minimum allowed frequency penalty.
     *
     * @return the minimum frequency penalty as a {@code double}
     */
    public double getMinFrequencyPenalty() {
        return minFrequencyPenalty;
    }

    /**
     * Retrieves the maximum allowed frequency penalty.
     *
     * @return the maximum frequency penalty as a {@code double}
     */
    public double getMaxFrequencyPenalty() {
        return maxFrequencyPenalty;
    }

    /**
     * Retrieves the minimum allowed presence penalty.
     *
     * @return the minimum presence penalty as a {@code double}
     */
    public double getMinPresencePenalty() {
        return minPresencePenalty;
    }

    /**
     * Retrieves the maximum allowed presence penalty.
     *
     * @return the maximum presence penalty as a {@code double}
     */
    public double getMaxPresencePenalty() {
        return maxPresencePenalty;
    }

    /**
     * Retrieves the identifier of this model.
     *
     * @return the model identifier as a {@link String}
     */
    public String getModel() {
        return model;
    }

    /**
     * Retrieves the cost of input tokens for this model.
     *
     * @return the input price as a {@link BigDecimal}
     */
    public BigDecimal getInputPrice() {
        return inputPrice;
    }

    /**
     * Retrieves the cost of cached input tokens for this model.
     *
     * @return the cached input price as a {@link BigDecimal}, or {@code null} if not applicable
     */
    public BigDecimal getCachedInputPrice() {
        return cachedInputPrice;
    }

    /**
     * Retrieves the cost of output tokens for this model.
     *
     * @return the output price as a {@link BigDecimal}
     */
    public BigDecimal getOutputPrice() {
        return outputPrice;
    }

    /**
     * Retrieves the maximum number of output tokens allowed for this model.
     *
     * @return the maximum output tokens as an {@code int}
     */
    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    /**
     * Retrieves the size of the context window for this model.
     *
     * @return the context window size as an {@code int}
     */
    public int getContextWindow() {
        return contextWindow;
    }

    /**
     * Checks if the given model name is valid.
     *
     * @param model the model name to validate
     * @return {@code true} if the model is valid, otherwise {@code false}
     */
    public boolean checkModel(String model) {
        if (model == null || model.isBlank()) return false;
        return models.contains(model);
    }

    /**
     * Checks if the given input string is valid based on the model's token limits.
     *
     * @param input the input string to validate
     * @return {@code true} if the input is valid, otherwise {@code false}
     */
    public boolean checkInput(String input) {
        if (input == null || input.isBlank()) return false;
        return input.length() <= maxOutputTokens;
    }

    /**
     * Checks if the given temperature value is within the valid range.
     *
     * @param temperature the temperature value to validate
     * @return {@code true} if the temperature is valid, otherwise {@code false}
     */
    public boolean checkTemperature(Double temperature) {
        if (temperature == null) return false;
        return temperature >= minTemperature && temperature <= maxTemperature;
    }

    /**
     * Checks if the given top-p value is within the valid range.
     *
     * @param topP the top-p value to validate
     * @return {@code true} if the top-p is valid, otherwise {@code false}
     */
    public boolean checkTopP(Integer topP) {
        if (topP == null) return false;
        return topP >= minTopP && topP <= maxTopP;
    }

    /**
     * Checks if the given frequency penalty is within the valid range.
     *
     * @param frequencyPenalty the frequency penalty to validate
     * @return {@code true} if the frequency penalty is valid, otherwise {@code false}
     */
    public boolean checkFrequencyPenalty(Double frequencyPenalty) {
        if (frequencyPenalty == null) return false;
        return frequencyPenalty >= minFrequencyPenalty && frequencyPenalty <= maxFrequencyPenalty;
    }

    /**
     * Checks if the given presence penalty is within the valid range.
     *
     * @param presencePenalty the presence penalty to validate
     * @return {@code true} if the presence penalty is valid, otherwise {@code false}
     */
    public boolean checkPresencePenalty(Double presencePenalty) {
        if (presencePenalty == null) return false;
        return presencePenalty >= minPresencePenalty && presencePenalty <= maxPresencePenalty;
    }

    /**
     * Checks if the given token count is within the maximum token limit.
     *
     * @param tokens the token count to validate
     * @return {@code true} if the token count is valid, otherwise {@code false}
     */
    public boolean checkTokens(Integer tokens) {
        if (tokens == null) return false;
        return tokens <= maxOutputTokens;
    }
}