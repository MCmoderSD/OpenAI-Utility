package de.MCmoderSD.OpenAI.enums;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * Enum representing various speech synthesis models and their associated properties.
 */
@SuppressWarnings({"ALL"})
public enum SpeechModel {

    /**
     * Basic text-to-speech model.
     */
    TTS("tts-1", 0.015),

    /**
     * High-definition text-to-speech model.
     */
    TTS_HD("tts-1-hd", 0.03);

    // Attributes
    private final HashSet<String> models;
    private final int maxCharacters;
    private final HashSet<String> voices;
    private final HashSet<String> formats;
    private final double minSpeed;
    private final double maxSpeed;
    private final HashSet<String> languages;
    private final String model;
    private final BigDecimal price;

    /**
     * Constructor to initialize the model with its attributes.
     *
     * @param model the model identifier (e.g., "tts-1")
     * @param price the price per character for the model
     * @throws IllegalArgumentException if the model identifier is invalid
     */
    SpeechModel(String model, double price) {

        // Initialize attributes
        models = new HashSet<>();
        maxCharacters = 4096;
        voices = new HashSet<>();
        formats = new HashSet<>();
        minSpeed = 0.25;
        maxSpeed = 4.0;
        languages = new HashSet<>();

        // Set models
        models.add("tts-1");
        models.add("tts-1-hd");

        // Set voices
        voices.add("alloy");
        voices.add("echo");
        voices.add("fable");
        voices.add("onyx");
        voices.add("nova");
        voices.add("shimmer");

        // Set formats
        formats.add("mp3");
        formats.add("opus");
        formats.add("aac");
        formats.add("flac");
        formats.add("wav");
        formats.add("pcm");

        // Set languages
        languages.add("Afrikaans");
        languages.add("Arabic");
        languages.add("Armenian");
        languages.add("Azerbaijani");
        languages.add("Belarusian");
        languages.add("Bosnian");
        languages.add("Bulgarian");
        languages.add("Catalan");
        languages.add("Chinese");
        languages.add("Croatian");
        languages.add("Czech");
        languages.add("Danish");
        languages.add("Dutch");
        languages.add("English");
        languages.add("Estonian");
        languages.add("Finnish");
        languages.add("French");
        languages.add("Galician");
        languages.add("German");
        languages.add("Greek");
        languages.add("Hebrew");
        languages.add("Hindi");
        languages.add("Hungarian");
        languages.add("Icelandic");
        languages.add("Indonesian");
        languages.add("Italian");
        languages.add("Japanese");
        languages.add("Kannada");
        languages.add("Kazakh");
        languages.add("Korean");
        languages.add("Latvian");
        languages.add("Lithuanian");
        languages.add("Macedonian");
        languages.add("Malay");
        languages.add("Marathi");
        languages.add("Maori");
        languages.add("Nepali");
        languages.add("Norwegian");
        languages.add("Persian");
        languages.add("Polish");
        languages.add("Portuguese");
        languages.add("Romanian");
        languages.add("Russian");
        languages.add("Serbian");
        languages.add("Slovak");
        languages.add("Slovenian");
        languages.add("Spanish");
        languages.add("Swahili");
        languages.add("Swedish");
        languages.add("Tagalog");
        languages.add("Tamil");
        languages.add("Thai");
        languages.add("Turkish");
        languages.add("Ukrainian");
        languages.add("Urdu");
        languages.add("Vietnamese");
        languages.add("Welsh");

        // Set Attributes
        if (models.contains(model)) this.model = model;
        else throw new IllegalArgumentException("Invalid model");
        this.price = new BigDecimal(price).movePointLeft(4);
    }

    /**
     * Calculates the cost for synthesizing a given number of characters.
     *
     * @param characters the number of characters to synthesize
     * @return the total cost as a {@link BigDecimal}
     */
    public BigDecimal calculateCost(int characters) {
        return price.multiply(new BigDecimal(characters));
    }


    /**
     * Retrieves the set of supported models.
     *
     * @return a {@link HashSet} of model identifiers
     */
    public HashSet<String> getModels() {
        return models;
    }

    /**
     * Retrieves the maximum number of characters allowed per synthesis request.
     *
     * @return the maximum number of characters
     */
    public int getMaxCharacters() {
        return maxCharacters;
    }

    /**
     * Retrieves the set of supported voices.
     *
     * @return a {@link HashSet} of voice identifiers
     */
    public HashSet<String> getVoices() {
        return voices;
    }

    /**
     * Retrieves the set of supported output formats.
     *
     * @return a {@link HashSet} of format identifiers
     */
    public HashSet<String> getFormats() {
        return formats;
    }

    /**
     * Retrieves the minimum playback speed.
     *
     * @return the minimum speed as a {@code double}
     */
    public double getMinSpeed() {
        return minSpeed;
    }

    /**
     * Retrieves the maximum playback speed.
     *
     * @return the maximum speed as a {@code double}
     */
    public double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Retrieves the set of supported languages.
     *
     * @return a {@link HashSet} of language names
     */
    public HashSet<String> getLanguages() {
        return languages;
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
     * Retrieves the price per character for the model.
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
     * Validates if the given input text is non-blank and within the character limit.
     *
     * @param input the input text to validate
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    public boolean checkInput(String input) {
        return !input.isBlank() && input.length() <= maxCharacters;
    }

    /**
     * Validates if the given voice identifier is supported.
     *
     * @param voice the voice identifier to validate
     * @return {@code true} if the voice is supported, {@code false} otherwise
     */
    public boolean checkVoice(String voice) {
        if (voice == null || voice.isBlank()) return false;
        return voices.contains(voice);
    }

    /**
     * Validates if the given output format is supported.
     *
     * @param format the format identifier to validate
     * @return {@code true} if the format is supported, {@code false} otherwise
     */
    public boolean checkFormat(String format) {
        if (format == null || format.isBlank()) return false;
        return formats.contains(format);
    }

    /**
     * Validates if the given playback speed is within the supported range.
     *
     * @param speed the playback speed to validate
     * @return {@code true} if the speed is valid, {@code false} otherwise
     */
    public boolean checkSpeed(Double speed) {
        if (speed == null) return false;
        return speed >= minSpeed && speed <= maxSpeed;
    }

    /**
     * Validates if the given language is supported.
     *
     * @param language the language name to validate
     * @return {@code true} if the language is supported, {@code false} otherwise
     */
    public boolean checkLanguage(String language) {
        if (language == null || language.isBlank()) return false;
        return languages.contains(language);
    }
}