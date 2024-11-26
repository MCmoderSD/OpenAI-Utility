package de.MCmoderSD.OpenAI.modules;

import com.fasterxml.jackson.databind.JsonNode;

import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.service.OpenAiService;

import de.MCmoderSD.JavaAudioLibrary.AudioFile;
import de.MCmoderSD.OpenAI.enums.TranscriptionModel;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * This class provides functionality for transcribing audio into text using OpenAI's transcription models.
 * It handles the creation of transcription requests, validates parameters, and provides methods
 * to process and transcribe audio files.
 */
@SuppressWarnings({"ALL"})
public class Transcription {

    // Associations
    private final TranscriptionModel model;
    private final OpenAiService service;

    // Attributes
    private final JsonNode config;
    private final String user;

    /**
     * Constructor to initialize the Transcription object with the necessary associations and attributes.
     *
     * @param model  The transcription model to use for transcription (e.g., speech-to-text).
     * @param config Configuration settings (e.g., prompt, language, temperature).
     * @param service The OpenAiService instance to interact with the OpenAI API.
     * @param user   The user associated with the transcription.
     */
    public Transcription(TranscriptionModel model, JsonNode config, OpenAiService service, String user) {

        // Set Associations
        this.model = model;
        this.service = service;

        // Set Attributes
        this.config = config;
        this.user = user;
    }

    /**
     * Creates a transcription request using the provided audio file and parameters.
     *
     * @param audioFile The audio file to transcribe.
     * @param prompt    The prompt to guide the transcription (optional).
     * @param language  The language of the audio file (optional).
     * @param temperature The temperature to control the randomness of the result (optional).
     * @return The transcription result containing the transcribed text.
     */
    private TranscriptionResult createTranscription(File audioFile, String prompt, String language, Double temperature) {

        // Request
        CreateTranscriptionRequest request = CreateTranscriptionRequest
                .builder()                          // Builder
                .model(model.getModel())            // Model
                .prompt(prompt)                     // Prompt
                .language(language.toLowerCase())   // Language
                .temperature(temperature)           // Temperature
                .build();                           // Build

        // Result
        return service.createTranscription(request, audioFile);
    }

    /**
     * Validates the parameters for transcription.
     *
     * @param file       The audio file to transcribe.
     * @param language   The language of the audio.
     * @param temperature The temperature for transcription randomness.
     * @return True if the parameters are valid, false otherwise.
     */
    public boolean disprove(File file, String language, double temperature) {

        // Check File
        if (file == null) throw new IllegalArgumentException("Input is null");
        if (!model.checkInput(file)) throw new IllegalArgumentException("Invalid input");

        // Check Language
        if (language == null) throw new IllegalArgumentException("Language is null");
        if (!model.checkLanguage(language)) throw new IllegalArgumentException("Invalid language");

        // Check Temperature
        if (!model.checkTemperature(temperature)) throw new IllegalArgumentException("Invalid temperature");

        // Disapprove parameters
        return false;
    }

    /**
     * Transcribes the audio from an AudioFile object to text using default or provided parameters.
     *
     * @param audioFile The audio file to transcribe.
     * @param prompt    The prompt to guide the transcription (optional).
     * @param language  The language of the audio file (optional).
     * @param temperature The temperature to control the randomness of the result (optional).
     * @return The transcribed text.
     */
    public String transcribe(AudioFile audioFile, @Nullable String prompt, @Nullable String language, @Nullable Double temperature) {

        // Get Temp Directory
        String tmpDir = System.getProperty("java.io.tmpdir");
        String fileName = getSHA256(audioFile.getAudioData()) + ".wav";
        String path = String.format("%s/.%s", tmpDir, fileName);

        // Export Audio File
        File file;
        try {
            file = audioFile.export(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Check Parameters
        prompt = prompt == null ? config.get("prompt").asText() : prompt;
        language = language == null ? config.get("language").asText() : language;
        temperature = temperature == null ? config.get("temperature").asDouble() : temperature;
        if (disprove(file, language, temperature)) throw new IllegalArgumentException("Invalid parameters");

        // Create Transcription
        TranscriptionResult result = createTranscription(file, prompt, language, temperature);

        // Delete File
        file.deleteOnExit();

        // Return Transcription
        return result.getText();
    }

    /**
     * Transcribes the audio data (in byte array format) to text.
     *
     * @param audioData The audio data as a byte array.
     * @param prompt    The prompt to guide the transcription (optional).
     * @param language  The language of the audio file (optional).
     * @param temperature The temperature to control the randomness of the result (optional).
     * @return The transcribed text.
     */
    public String transcribe(byte[] audioData, @Nullable String prompt, @Nullable String language, @Nullable Double temperature) {
        return transcribe(new AudioFile(audioData), prompt, language, temperature);
    }

    /**
     * Transcribes the audio file to text using default parameters.
     *
     * @param audioFile The audio file to transcribe.
     * @return The transcribed text.
     */
    public String transcribe(AudioFile audioFile) {
        return transcribe(audioFile, null, null, null);
    }

    /**
     * Transcribes the audio data (in byte array format) to text using default parameters.
     *
     * @param audioData The audio data to transcribe.
     * @return The transcribed text.
     */
    public String transcribe(byte[] audioData) {
        return transcribe(new AudioFile(audioData));
    }

    /**
     * Calculates the SHA-256 hash of the given byte array.
     *
     * @param data The byte array to hash.
     * @return The SHA-256 hash as a hexadecimal string.
     */
    private static String getSHA256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found");
            return null;
        }
    }

    /**
     * Retrieves the configuration settings for transcription.
     *
     * @return The configuration as a JsonNode object.
     */
    public JsonNode getConfig() {
        return config;
    }

    /**
     * Retrieves the transcription model associated with this instance.
     *
     * @return The TranscriptionModel.
     */
    public TranscriptionModel getModel() {
        return model;
    }

    /**
     * Retrieves the OpenAiService instance used to interact with the OpenAI API.
     *
     * @return The OpenAiService instance.
     */
    public OpenAiService getService() {
        return service;
    }

    /**
     * Calculates the cost of transcription based on the duration of the audio (in seconds).
     *
     * @param seconds The duration of the audio in seconds.
     * @return The price as a BigDecimal.
     */
    public BigDecimal calculatePrice(int seconds) {
        return model.calculateCost(seconds);
    }

    /**
     * Calculates the cost of transcription based on the AudioFile object.
     *
     * @param audioFile The AudioFile object to transcribe.
     * @return The price as a BigDecimal.
     */
    public BigDecimal calculatePrice(AudioFile audioFile) {
        return model.calculateCost(audioFile);
    }
}