package de.MCmoderSD.OpenAI.enums;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashSet;

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

    // Attributes
    private final HashSet<String> models;
    private final double minTemperature;
    private final double maxTemperature;
    private final double minTopP;
    private final double maxTopP;
    private final double minFrequencyPenalty;
    private final double maxFrequencyPenalty;
    private final double minPresencePenalty;
    private final double maxPresencePenalty;

    // Constants
    private final String model;
    private final BigDecimal inputPrice;
    private final BigDecimal cachedInputPrice;
    private final BigDecimal outputPrice;
    private final int maxOutputTokens;
    private final int contextWindow;

    // Constructor
    ChatModel(String model, double input, double output, int maxOutputTokens, int contextWindow) {
        this(model, input, null, output, maxOutputTokens, contextWindow);
    }

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

    // Methods
    public BigDecimal calculateCost(int inputTokens, int outputTokens) {
        return inputPrice.multiply(new BigDecimal(inputTokens)).add(outputPrice.multiply(new BigDecimal(outputTokens)));
    }

    public BigDecimal calculateCost(int tokens) {
        return inputPrice.multiply(new BigDecimal(tokens)).add(outputPrice.multiply(new BigDecimal(tokens)));
    }

    // Getter
    public HashSet<String> getModels() {
        return models;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinTopP() {
        return minTopP;
    }

    public double getMaxTopP() {
        return maxTopP;
    }

    public double getMinFrequencyPenalty() {
        return minFrequencyPenalty;
    }

    public double getMaxFrequencyPenalty() {
        return maxFrequencyPenalty;
    }

    public double getMinPresencePenalty() {
        return minPresencePenalty;
    }

    public double getMaxPresencePenalty() {
        return maxPresencePenalty;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getInputPrice() {
        return inputPrice;
    }

    public BigDecimal getCachedInputPrice() {
        return cachedInputPrice;
    }

    public BigDecimal getOutputPrice() {
        return outputPrice;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public int getContextWindow() {
        return contextWindow;
    }

    // Check
    public boolean checkModel(String model) {
        if (model == null || model.isBlank()) return false;
        return models.contains(model);
    }

    public boolean checkInput(String input) {
        if (input == null || input.isBlank()) return false;
        return input.length() <= maxOutputTokens;
    }

    public boolean checkTemperature(Double temperature) {
        if (temperature == null) return false;
        return temperature >= minTemperature && temperature <= maxTemperature;
    }

    public boolean checkTopP(Integer topP) {
        if (topP == null) return false;
        return topP >= minTopP && topP <= maxTopP;
    }

    public boolean checkFrequencyPenalty(Double frequencyPenalty) {
        if (frequencyPenalty == null) return false;
        return frequencyPenalty >= minFrequencyPenalty && frequencyPenalty <= maxFrequencyPenalty;
    }

    public boolean checkPresencePenalty(Double presencePenalty) {
        if (presencePenalty == null) return false;
        return presencePenalty >= minPresencePenalty && presencePenalty <= maxPresencePenalty;
    }

    public boolean checkTokens(Integer tokens) {
        if (tokens == null) return false;
        return tokens <= maxOutputTokens;
    }
}