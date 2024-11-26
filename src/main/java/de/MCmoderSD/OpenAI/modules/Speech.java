package de.MCmoderSD.OpenAI.modules;

import com.fasterxml.jackson.databind.JsonNode;

import com.theokanning.openai.audio.CreateSpeechRequest;
import com.theokanning.openai.service.OpenAiService;

import de.MCmoderSD.JavaAudioLibrary.AudioFile;
import de.MCmoderSD.OpenAI.enums.SpeechModel;

import okhttp3.ResponseBody;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
/**
 * This class provides functionality for converting text to speech using OpenAI's speech models.
 * It handles parameter validation, speech generation, and returning audio files.
 */
@SuppressWarnings({"ALL"})
public class Speech {

    // Associations
    private final SpeechModel model;
    private final OpenAiService service;

    // Attributes
    private final JsonNode config;
    private final String user;

    /**
     * Constructor to initialize the Speech object with the necessary associations and attributes.
     *
     * @param model  The speech model to use for conversion (e.g., GPT-based speech synthesis).
     * @param config Configuration settings (e.g., default voice, format, speed).
     * @param service The OpenAiService instance to interact with the OpenAI API.
     * @param user   The user associated with the speech generation.
     */
    public Speech(SpeechModel model, JsonNode config, OpenAiService service, String user) {

        // Set Associations
        this.model = model;
        this.service = service;

        // Set Attributes
        this.config = config;
        this.user = user;
    }

    /**
     * Creates a speech request using the provided parameters.
     *
     * @param input    The input text to convert to speech.
     * @param voice    The voice to use for speech generation.
     * @param format   The format of the response (e.g., audio format).
     * @param speed    The speed of the generated speech.
     * @return The response body containing the audio data.
     */
    private ResponseBody createSpeech(String input, String voice, String format, double speed) {

        // Request
        CreateSpeechRequest request = CreateSpeechRequest
                .builder()                  // Builder
                .model(model.getModel())    // Model
                .input(input)               // Input
                .voice(voice)               // Voice
                .responseFormat(format)     // Format
                .speed(speed)               // Speed
                .build();                   // Build

        // Result
        return service.createSpeech(request);
    }

    /**
     * Validates the input parameters for speech generation.
     *
     * @param input    The input text to convert to speech.
     * @param voice    The voice to use for speech generation.
     * @param format   The format of the response (e.g., audio format).
     * @param speed    The speed of the generated speech.
     * @return True if parameters are valid, false otherwise.
     */
    public boolean disprove(String input, String voice, String format, double speed) {

        // Check Input
        if (input == null) throw new IllegalArgumentException("Input is null");
        if (!model.checkInput(input)) throw new IllegalArgumentException("Invalid input");

        // Check Voice
        if (voice == null) throw new IllegalArgumentException("Voice is null");
        if (!model.checkVoice(voice)) throw new IllegalArgumentException("Invalid voice");

        // Check Format
        if (format == null) throw new IllegalArgumentException("Format is null");
        if (!model.checkFormat(format)) throw new IllegalArgumentException("Invalid format");

        // Check Speed
        if (!model.checkSpeed(speed)) throw new IllegalArgumentException("Invalid speed");

        // Disapprove parameters
        return false;
    }

    /**
     * Converts the input text to speech and returns the resulting audio file.
     *
     * @param input    The input text to convert to speech.
     * @param voice    The voice to use for speech generation (optional).
     * @param format   The format of the response (optional).
     * @param speed    The speed of the generated speech (optional).
     * @return An AudioFile containing the generated speech.
     */
    public AudioFile speak(String input, @Nullable String voice, @Nullable String format, @Nullable Double speed) {

        // Check Parameters
        voice = voice == null ? config.get("voice").asText() : voice;
        format = format == null ? config.get("format").asText() : format;
        speed = speed == null ? config.get("speed").asDouble() : speed;
        if (disprove(input, voice, format, speed)) throw new IllegalArgumentException("Invalid parameters");

        // Get response
        ResponseBody response = createSpeech(input, voice, format, speed);
        if (response == null) throw new IllegalArgumentException("Invalid response");

        // Get audio file
        AudioFile audioFile;
        try {
            audioFile = new AudioFile(response.bytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Return audio file
        return audioFile;
    }
    /**
     * Converts text to speech using the default parameters.
     *
     * @param input The text to convert to speech.
     * @return An AudioFile containing the generated speech.
     */
    public AudioFile speak(String input) {
        return speak(input, null, null, null);
    }

    /**
     * Retrieves the configuration settings for speech generation.
     *
     * @return The configuration as a JsonNode object.
     */
    public JsonNode getConfig() {
        return config;
    }

    /**
     * Retrieves the speech model associated with this instance.
     *
     * @return The SpeechModel.
     */
    public SpeechModel getModel() {
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
     * Calculates the cost of generating speech based on the input length.
     *
     * @param input The input text to convert to speech.
     * @return The price as a BigDecimal.
     */
    public BigDecimal calculatePrice(String input) {
        return model.calculateCost(input.length());
    }

    /**
     * Calculates the cost of generating speech based on the number of characters.
     *
     * @param characters The number of characters in the input text.
     * @return The price as a BigDecimal.
     */
    public BigDecimal calculatePrice(int characters) {
        return model.calculateCost(characters);
    }
}