import com.fasterxml.jackson.databind.JsonNode;

import de.MCmoderSD.OpenAI.OpenAI;
import de.MCmoderSD.OpenAI.enums.ImageModel;
import de.MCmoderSD.OpenAI.modules.Chat;
import de.MCmoderSD.OpenAI.modules.Image;
import de.MCmoderSD.OpenAI.modules.Speech;
import de.MCmoderSD.OpenAI.modules.Transcription;
import de.MCmoderSD.JavaAudioLibrary.AudioFile;

import java.util.HashSet;

public class Main {

    // Attributes
    private static JsonNode config;

    public Main() {

        // Attributes
        OpenAI openAI = new OpenAI(config);
    }

    public static void chatExample(OpenAI openAI) {

        // Get Chat
        Chat chat = openAI.getChat();

        // Simple Prompt
        String prompt = chat.prompt(
                "MCmoderSD",                        // User
                "Translate this text to German: ",  // Instruction
                "Hello, how are you?",              // Prompt
                1,                                  // Temperature
                4096,                              // Max Tokens
                1,                                  // Top P
                1,                                  // Frequency Penalty
                1                                   // Presence Penalty
        );

        // Conversation
        String conversation = chat.converse(
                1,                                  // Conversation ID
                4,                                  // Max Turns
                16384,                              // Max Tokens
                "MCmoderSD",                        // User
                "Translate this text to German: ",  // Instruction
                "Hello, how are you?",              // Prompt
                1,                                  // Temperature
                4096,                              // Max Tokens
                1,                                  // Top P
                1,                                  // Frequency Penalty
                1                                   // Presence Penalty
        );

        // Clear Conversation
        chat.clearConversation(1);
    }

    public static void imageExample(OpenAI openAI) {

        // Get Image
        Image image = openAI.getImage();

        // Image Prompt
        HashSet<String> imageUrls = image.generate(
                "MCmoderSD",                                        // User
                "Generate a picture of a cat",                      // Prompt
                1,                                                  // Amount
                ImageModel.Resolution.RES_512x512.getResolution()   // Resolution
        );
    }

    public static void speechExample(OpenAI openAI) {

        // Get Speech
        Speech speech = openAI.getSpeech();

        // Speech Prompt
        AudioFile audioFile = speech.speak(
                "Hello, how are you",   // Input
                "alloy",                // Voice
                "wav",                  // Format
                1                       // Speed
        );

        // Play Audio
        audioFile.play();
    }

    public static void transcribeExample(OpenAI openAI, AudioFile input) {

        // Get Transcription
        Transcription transcription = openAI.getTranscription();

        // Transcribe
        String output = transcription.transcribe(
                input,                              // Audio File
                "Translate this text to German: ",  // Prompt
                "en",                               // Language
                1                                   // Temperature
        );
    }
}