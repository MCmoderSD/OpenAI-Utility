package de.MCmoderSD.OpenAI.enums;

import java.math.BigDecimal;
import java.util.HashSet;

@SuppressWarnings("ALL")
public enum SpeechModel {

    // Models
    TTS("tts-1", 0.015),
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

    // Constructor
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

    // Methods
    public BigDecimal calculateCost(int characters) {
        return price.multiply(new BigDecimal(characters));
    }

    // Getter
    public HashSet<String> getModels() {
        return models;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public HashSet<String> getVoices() {
        return voices;
    }

    public HashSet<String> getFormats() {
        return formats;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public HashSet<String> getLanguages() {
        return languages;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getPrice() {
        return price;
    }

    // Check
    public boolean checkModel(String model) {
        if (model == null || model.isBlank()) return false;
        return models.contains(model);
    }

    public boolean checkInput(String input) {
        return !input.isBlank() && input.length() <= maxCharacters;
    }

    public boolean checkVoice(String voice) {
        if (voice == null || voice.isBlank()) return false;
        return voices.contains(voice);
    }

    public boolean checkFormat(String format) {
        if (format == null || format.isBlank()) return false;
        return formats.contains(format);
    }

    public boolean checkSpeed(Double speed) {
        if (speed == null) return false;
        return speed >= minSpeed && speed <= maxSpeed;
    }

    public boolean checkLanguage(String language) {
        if (language == null || language.isBlank()) return false;
        return languages.contains(language);
    }
}