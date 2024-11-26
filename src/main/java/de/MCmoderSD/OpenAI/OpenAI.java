package de.MCmoderSD.OpenAI;

import com.fasterxml.jackson.databind.JsonNode;

import com.theokanning.openai.service.OpenAiService;

import de.MCmoderSD.OpenAI.enums.ChatModel;
import de.MCmoderSD.OpenAI.enums.ImageModel;
import de.MCmoderSD.OpenAI.enums.SpeechModel;
import de.MCmoderSD.OpenAI.enums.TranscriptionModel;

import de.MCmoderSD.OpenAI.modules.Chat;
import de.MCmoderSD.OpenAI.modules.Image;
import de.MCmoderSD.OpenAI.modules.Speech;
import de.MCmoderSD.OpenAI.modules.Transcription;

/**
 * Main class to interact with various OpenAI models for chat, image generation, speech synthesis,
 * and transcription services. It manages the activation of each module and initializes the required
 * services based on the provided configuration.
 */
@SuppressWarnings({"ALL"})
public class OpenAI {

    // Constants
    private final JsonNode config;
    private final String user;

    // Attributes
    private final OpenAiService service;

    // Modules
    private final boolean chatActive;
    private final boolean imageActive;
    private final boolean speechActive;
    private final boolean transcriptionActive;

    // Associations
    private final Chat chat;
    private final Image image;
    private final Speech speech;
    private final Transcription transcription;

    // Enums
    private final ChatModel chatModel;
    private final ImageModel imageModel;
    private final SpeechModel speechModel;
    private final TranscriptionModel transcriptionModel;

    /**
     * Constructor to initialize the OpenAI service and modules based on the provided configuration.
     * It initializes the chat, image, speech, and transcription modules if they are present in the configuration.
     *
     * @param config The configuration settings, including the API key, user details, and module configurations.
     */
    public OpenAI(JsonNode config) {

        // Set Config and User
        this.config = config;
        user = config.get("user").asText();

        // Initialize OpenAI Service
        service = new OpenAiService(config.get("apiKey").asText());

        // Check Modules
        chatActive = config.has("chat");
        imageActive = config.has("image");
        speechActive = config.has("speech");
        transcriptionActive = config.has("transcription");

        // Initialize Chat Module
        if (chatActive) {

            // Get Chat Config
            JsonNode chatConfig = config.get("chat");
            String model = chatConfig.get("model").asText();

            // Check Chat Model
            if (model == null) throw new IllegalArgumentException("Chat model is null");
            if (model.isBlank()) throw new IllegalArgumentException("Chat model is empty");

            // Set Chat Model
            chatModel = switch (model) {
                case "gpt-4o" -> ChatModel.GPT_4O;
                case "gpt-4o-2024-11-20" -> ChatModel.GPT_4O_2024_11_20;
                case "gpt-4o-2024-08-06" -> ChatModel.GPT_4O_2024_08_06;
                case "gpt-4o-2024-05-13" -> ChatModel.GPT_4O_2024_05_13;
                case "gpt-4o-mini" -> ChatModel.GPT_4O_MINI;
                case "gpt-4o-mini-2024-07-18" -> ChatModel.GPT_4O_MINI_2024_07_18;
                case "o1-preview" -> ChatModel.O1_PREVIEW;
                case "o1-preview-2024-09-12" -> ChatModel.O1_PREVIEW_2024_09_12;
                case "o1-mini" -> ChatModel.O1_MINI;
                case "o1-mini-2024-09-12" -> ChatModel.O1_MINI_2024_09_12;
                case "chatgpt-4o-latest" -> ChatModel.CHATGPT_4O_LATEST;
                default -> throw new IllegalArgumentException("Invalid chat model");
            };

            // Initialize Chat
            chat = new Chat(chatModel, chatConfig, service, user);
        } else {
            chatModel = null;
            chat = null;
        }

        // Initialize Image Module
        if (imageActive) {

            // Get Image Config
            JsonNode imageConfig = config.get("image");
            String model = imageConfig.get("model").asText();

            // Check Image Model
            if (model == null) throw new IllegalArgumentException("Image model is null");
            if (model.isBlank())
                throw new IllegalArgumentException("Image model is empty");

            // Set Image Model
            imageModel = switch (model) {
                case "dall-e-2" -> ImageModel.DALL_E_2;
                case "dall-e-3" -> ImageModel.DALL_E_3;
                default -> throw new IllegalArgumentException("Invalid image model");
            };

            // Initialize Image
            image = new Image(imageModel, imageConfig, service, user);
        } else {
            imageModel = null;
            image = null;
        }

        // Initialize Speech Module
        if (speechActive) {

            // Get Speech Config
            JsonNode speechConfig = config.get("speech");
            String model = speechConfig.get("model").asText();

            // Check Speech Model
            if (model == null) throw new IllegalArgumentException("Speech model is null");
            if (model.isBlank()) throw new IllegalArgumentException("Speech model is empty");

            // Set TTS Model
            speechModel = switch (model) {
                case "tts-1" -> SpeechModel.TTS;
                case "tts-1-hd" -> SpeechModel.TTS_HD;
                default -> throw new IllegalArgumentException("Invalid Speech model");
            };

            // Initialize Speech
            speech = new Speech(speechModel, speechConfig, service, user);
        } else {
            speechModel = null;
            speech = null;
        }

        // Initialize Transcription Module
        if (transcriptionActive) {

            // Get Transcription Config
            JsonNode transcriptionConfig = config.get("transcription");
            String model = transcriptionConfig.get("model").asText();

            // Check Transcription Model
            if (model == null) throw new IllegalArgumentException("Transcription model is null");
            if (model.isBlank()) throw new IllegalArgumentException("Transcription model is empty");

            // Set Transcription Model
            transcriptionModel = switch (model) {
                case "whisper-1" -> TranscriptionModel.WHISPER;
                default -> throw new IllegalArgumentException("Invalid transcription model");
            };

            // Initialize Transcription
            transcription = new Transcription(transcriptionModel, transcriptionConfig, service, user);
        } else {
            transcriptionModel = null;
            transcription = null;
        }
    }

    /**
     * Retrieves the configuration settings for the OpenAI service.
     *
     * @return The configuration as a JsonNode object.
     */
    public JsonNode getConfig() {
        return config;
    }

    /**
     * Checks if the OpenAI service is active (initialized).
     *
     * @return True if the service is initialized, false otherwise.
     */
    public boolean isActive() {
        return service != null;
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
     * Retrieves the user associated with the OpenAI service.
     *
     * @return The user as a string.
     */
    public String getUser() {
        return user;
    }

    /**
     * Retrieves the Chat module instance.
     *
     * @return The Chat module, or null if not active.
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * Retrieves the Image module instance.
     *
     * @return The Image module, or null if not active.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Retrieves the Speech module instance.
     *
     * @return The Speech module, or null if not active.
     */
    public Speech getSpeech() {
        return speech;
    }

    /**
     * Retrieves the Transcription module instance.
     *
     * @return The Transcription module, or null if not active.
     */
    public Transcription getTranscription() {
        return transcription;
    }

    /**
     * Checks if the Chat module is active.
     *
     * @return True if the Chat module is active, false otherwise.
     */
    public boolean isChatActive() {
        return chatActive;
    }

    /**
     * Checks if the Image module is active.
     *
     * @return True if the Image module is active, false otherwise.
     */
    public boolean isImageActive() {
        return imageActive;
    }

    /**
     * Checks if the Speech module is active.
     *
     * @return True if the Speech module is active, false otherwise.
     */
    public boolean isSpeechActive() {
        return speechActive;
    }

    /**
     * Checks if the Transcription module is active.
     *
     * @return True if the Transcription module is active, false otherwise.
     */
    public boolean isTranscriptionActive() {
        return transcriptionActive;
    }

    /**
     * Retrieves the Chat model used in the OpenAI service.
     *
     * @return The ChatModel enum.
     */
    public ChatModel getChatModel() {
        return chatModel;
    }

    /**
     * Retrieves the Image model used in the OpenAI service.
     *
     * @return The ImageModel enum.
     */
    public ImageModel getImageModel() {
        return imageModel;
    }

    /**
     * Retrieves the Speech model used in the OpenAI service.
     *
     * @return The SpeechModel enum.
     */
    public SpeechModel getSpeechModel() {
        return speechModel;
    }

    /**
     * Retrieves the Transcription model used in the OpenAI service.
     *
     * @return The TranscriptionModel enum.
     */
    public TranscriptionModel getTranscriptionModel() {
        return transcriptionModel;
    }
}