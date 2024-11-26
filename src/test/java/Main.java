import com.fasterxml.jackson.databind.JsonNode;

import de.MCmoderSD.json.JsonUtility;
import de.MCmoderSD.JavaAudioLibrary.AudioFile;

import de.MCmoderSD.OpenAI.OpenAI;
import de.MCmoderSD.OpenAI.modules.Chat;
import de.MCmoderSD.OpenAI.modules.Image;
import de.MCmoderSD.OpenAI.modules.Speech;
import de.MCmoderSD.OpenAI.modules.Transcription;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {

        // Load Config
        JsonNode config = JsonUtility.loadJson("/config.json", false);

        // Initialize OpenAI
        OpenAI openAI = new OpenAI(config);

        // Examples
        //chatExample(openAI);
        imageExample(openAI);
        //AudioFile testFile = speechExample(openAI);
        //transcriptionExample(openAI, testFile);
    }

    public static void chatExample(OpenAI openAI) throws InterruptedException {

        // Get Chat
        Chat chat = openAI.getChat();

        // Simple Prompt
        String prompt = chat.prompt("How are you doing today?");
        System.out.println(prompt + "\n");

        // Simple Prompt Stream
        chat.promptStream("This is a test!").forEach(chunk -> System.out.print(Chat.getContent(chunk)));
        Thread.sleep(2000); // Wait for Stream to Finish
        System.out.println("\n");

        // Custom Prompt
        String customPrompt = chat.prompt(
                "MCmoderSD",                                // User
                "Translate the following text into German: ",   // Prompt
                "Hello, how are you?",                          // Text
                1d,                                             // Temperature
                4096,                                           // Max Tokens
                1d,                                             // Top P
                1d,                                             // Frequency Penalty
                1d                                              // Presence Penalty
        );
        System.out.println(customPrompt + "\n");

        // Custom Prompt Stream
        chat.promptStream(
                "MCmoderSD",                                // User
                "Translate the following text into French: ",   // Prompt
                "Hello, how are you?",                          // Text
                1d,                                             // Temperature
                4096,                                           // Max Tokens
                1d,                                             // Top P
                1d,                                             // Frequency Penalty
                1d                                              // Presence Penalty
        ).forEach(chunk -> System.out.print(Chat.getContent(chunk)));
        Thread.sleep(2000); // Wait for Stream to Finish
        System.out.println("\n");

        // Start simple Conversation
        var id = 1; // Conversation ID
        chat.converse(id, "Hello, my name is MCmoderSD");

        // Continue Conversation as custom stream
        chat.converseStream(
                id,                 // Conversation ID
                5,                  // Max Calls
                16384,              // Max Tokens spend total
                "MCmoderSD",        // User
                null,               // Instruction
                "What is my name?", // Message
                1d,                 // Temperature
                4096,               // Max Tokens
                1d,                 // Top P
                1d,                 // Frequency Penalty
                1d                  // Presence Penalty
        ).forEach(chunk -> System.out.print(Chat.getContent(chunk)));
    }

    public static void imageExample(OpenAI openAI) {

        // Get Image
        Image image = openAI.getImage();

        // Generate Image
        HashSet<String > imageUrls = image.generate("A beautiful sunset over the ocean");
        imageUrls.forEach(System.out::println);

        // Generate Image with custom parameters (DALL-E 2)
        HashSet<String> customImageUrls = image.generate(
                "MCmoderSD",            // User
                "A cat, eating a donut",    // Prompt
                1,                          // Amount
                null,                       // Quality
                "512x512",                  // Resolution
                null                        // Style
        );
        customImageUrls.forEach(System.out::println);
    }

    public static AudioFile speechExample(OpenAI openAI) throws InterruptedException {

        // Get Speech
        Speech speech = openAI.getSpeech();

        // Generate TTS
        speech.speak("Hey, how are you?").play();
        Thread.sleep(2000); // Wait for Audio to Finish

        // Generate TTS with custom parameters
        return speech.speak(
                "This is a test recording",     // Text
                "onyx",                             // Voice
                "wav",                              // Format
                1d                                  // Speed
        );
    }

    public static void transcriptionExample(OpenAI openAI, AudioFile audio) {

        // Get Transcription
        Transcription transcription = openAI.getTranscription();

        // Transcribe Audio
        String text = transcription.transcribe(audio);
        System.out.println(text);

        // Transcribe Audio with custom parameters
        text = transcription.transcribe(
                audio,                          // Audio
                "What is the following text?",  // Prompt
                "en",                           // Language
                1d                              // Temperature
        );

        System.out.println(text);
    }
}