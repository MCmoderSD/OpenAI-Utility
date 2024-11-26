package de.MCmoderSD.OpenAI.enums;

import de.MCmoderSD.JavaAudioLibrary.AudioFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Enum representing transcription models and their associated properties.
 */
@SuppressWarnings("ALL")
public enum TranscriptionModel {

    /**
     * Whisper transcription model with per-second pricing.
     */
    WHISPER("whisper-1", 0.006);

    // Attributes
    private final HashSet<String> models;
    private final HashSet<String> languages;
    private final String model;
    private final double minTemperature;
    private final double maxTemperature;
    private final BigDecimal uploadLimit;
    private final BigDecimal price;

    /**
     * Constructor to initialize a transcription model.
     *
     * @param model the model identifier (e.g., "whisper-1")
     * @param price the price per second for the model
     * @throws IllegalArgumentException if the model identifier is invalid
     */
    TranscriptionModel(String model, double price) {

        // Initialize attributes
        models = new HashSet<>();
        languages = new HashSet<>();

        // Set models
        models.add("whisper-1");

        // Set languages
        // Set languages in ISO-639-1 format
        languages.add("af"); // Afrikaans
        languages.add("ar"); // Arabic
        languages.add("hy"); // Armenian
        languages.add("az"); // Azerbaijani
        languages.add("be"); // Belarusian
        languages.add("bs"); // Bosnian
        languages.add("bg"); // Bulgarian
        languages.add("ca"); // Catalan
        languages.add("zh"); // Chinese
        languages.add("hr"); // Croatian
        languages.add("cs"); // Czech
        languages.add("da"); // Danish
        languages.add("nl"); // Dutch
        languages.add("en"); // English
        languages.add("et"); // Estonian
        languages.add("fi"); // Finnish
        languages.add("fr"); // French
        languages.add("gl"); // Galician
        languages.add("de"); // German
        languages.add("el"); // Greek
        languages.add("he"); // Hebrew
        languages.add("hi"); // Hindi
        languages.add("hu"); // Hungarian
        languages.add("is"); // Icelandic
        languages.add("id"); // Indonesian
        languages.add("it"); // Italian
        languages.add("ja"); // Japanese
        languages.add("kn"); // Kannada
        languages.add("kk"); // Kazakh
        languages.add("ko"); // Korean
        languages.add("lv"); // Latvian
        languages.add("lt"); // Lithuanian
        languages.add("mk"); // Macedonian
        languages.add("ms"); // Malay
        languages.add("mr"); // Marathi
        languages.add("mi"); // Maori
        languages.add("ne"); // Nepali
        languages.add("no"); // Norwegian
        languages.add("fa"); // Persian
        languages.add("pl"); // Polish
        languages.add("pt"); // Portuguese
        languages.add("ro"); // Romanian
        languages.add("ru"); // Russian
        languages.add("sr"); // Serbian
        languages.add("sk"); // Slovak
        languages.add("sl"); // Slovenian
        languages.add("es"); // Spanish
        languages.add("sw"); // Swahili
        languages.add("sv"); // Swedish
        languages.add("tl"); // Tagalog
        languages.add("ta"); // Tamil
        languages.add("th"); // Thai
        languages.add("tr"); // Turkish
        languages.add("uk"); // Ukrainian
        languages.add("ur"); // Urdu
        languages.add("vi"); // Vietnamese
        languages.add("cy"); // Welsh

        // Set Attributes
        if (models.contains(model)) this.model = model;
        else throw new IllegalArgumentException("Invalid model");
        this.price = new BigDecimal(price).divide(new BigDecimal(60));
        this.uploadLimit = new BigDecimal(25).movePointRight(6);
        minTemperature = 0.0;
        maxTemperature = 2.0;
    }

    /**
     * Calculates the cost of transcribing an audio file based on its duration in seconds.
     *
     * @param seconds the duration of the audio in seconds
     * @return the total cost as a {@link BigDecimal}
     */
    public BigDecimal calculateCost(int seconds) {
        return price.multiply(new BigDecimal(seconds));
    }

    /**
     * Calculates the cost of transcribing an audio file based on its {@link AudioFile} representation.
     *
     * @param input the {@link AudioFile} to transcribe
     * @return the total cost as a {@link BigDecimal}
     */
    public BigDecimal calculateCost(AudioFile input) {
        return calculateCost((int) input.getDuration());
    }

    /**
     * Retrieves the set of supported transcription models.
     *
     * @return a {@link HashSet} containing the model identifiers
     */
    public HashSet<String> getModels() {
        return models;
    }

    /**
     * Retrieves the set of supported languages in ISO-639-1 format.
     *
     * @return a {@link HashSet} containing the supported language codes
     */
    public HashSet<String> getLanguages() {
        return languages;
    }

    /**
     * Retrieves the maximum upload file size in bytes.
     *
     * @return the upload limit as a {@link BigDecimal}
     */
    public BigDecimal getUploadLimit() {
        return uploadLimit;
    }

    /**
     * Retrieves the model identifier.
     *
     * @return the model identifier as a {@link String}
     */
    public String getModel() {
        return model;
    }

    /**
     * Retrieves the minimum temperature value for transcription configuration.
     *
     * @return the minimum temperature as a {@code double}
     */
    public double getMinTemperature() {
        return minTemperature;
    }

    /**
     * Retrieves the maximum temperature value for transcription configuration.
     *
     * @return the maximum temperature as a {@code double}
     */
    public double getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * Retrieves the price per second for the transcription model.
     *
     * @return the price as a {@link BigDecimal}
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Validates if the given model identifier is supported.
     *
     * @param model the model identifier to validate
     * @return {@code true} if the model is supported, {@code false} otherwise
     */
    public boolean checkModel(String model) {
        if (model == null || model.isBlank()) return false;
        return models.contains(model);
    }

    /**
     * Validates if the given temperature value is within the supported range.
     *
     * @param temperature the temperature value to validate
     * @return {@code true} if the temperature is valid, {@code false} otherwise
     */
    public boolean checkTemperature(Double temperature) {
        if (temperature == null) return false;
        return temperature >= minTemperature && temperature <= maxTemperature;
    }

    /**
     * Validates if the given file size is within the upload limit.
     *
     * @param input the file to validate
     * @return {@code true} if the file size is within the limit, {@code false} otherwise
     */
    public boolean checkSize(File input) {
        if (!input.exists() || !input.isFile()) return false;
        return input.length() < uploadLimit.longValue();
    }

    /**
     * Validates if the given {@link File} can be processed for transcription.
     *
     * @param input the file to validate
     * @return {@code true} if the file is valid, {@code false} otherwise
     */
    public boolean checkInput(File input) {
        return input.length() < uploadLimit.longValue();
    }

    /**
     * Validates if the given {@link AudioFile} can be processed for transcription.
     *
     * @param input the {@link AudioFile} to validate
     * @return {@code true} if the audio file is valid, {@code false} otherwise
     */
    public boolean checkInput(AudioFile input) {
        return input.getSize() < uploadLimit.longValue();
    }

    /**
     * Validates if the given language is supported.
     *
     * @param language the language code to validate (ISO-639-1 format)
     * @return {@code true} if the language is supported, {@code false} otherwise
     */
    public boolean checkLanguage(String language) {
        if (language == null || language.isBlank()) return false;
        return languages.contains(language.toLowerCase());
    }
}