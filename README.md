# OpenAI Utility
[![](https://jitpack.io/v/MCmoderSD/OpenAI-Utility.svg)](https://jitpack.io/#MCmoderSD/OpenAI-Utility)


## Description
This **Java Utility** provides seamless integration with **OpenAI API Services**, offering a straightforward approach to interact with various OpenAI features.

The utility leverages the [OpenAI Library](https://github.com/TheoKanning/openai-java) by [Theo Kanning](https://github.com/TheoKanning/). While this library is no longer actively maintained, this utility extends its functionality to ensure compatibility with the latest OpenAI services.

### Supported Features:
- **Chat API**: Generate conversational responses with advanced models.
- **Image API**: Create stunning visuals using OpenAI's image generation tools.
- **Speech API**: Convert text to speech with customizable voices and formats.
- **Transcription API**: Accurately transcribe audio files with support for multiple languages.

This utility simplifies complex interactions, making it easier than ever to harness the power of OpenAI in your Java projects.


## Usage

### Maven
Make sure you have the JitPack repository added to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add the dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>com.github.MCmoderSD</groupId>
    <artifactId>OpenAI-Utility</artifactId>
    <version>1.1.0</version>
</dependency>
```


## Configuration
To configure the utility, provide a `JsonNode` with the following structure:
```json
{
  "user": "YOUR_USERNAME",
  "apiKey": "YOUR_API_KEY",

  "chat": {
    "model": "gpt-4o-2024-11-20",
    "maxConversationCalls": 10,
    "maxTokenSpendingLimit": 8192,
    "temperature": 1,
    "maxOutputTokens": 120,
    "topP": 1,
    "frequencyPenalty": 0,
    "presencePenalty": 0,
    "instruction": "You are the best TwitchBot that ever existed!"
  },

  "image": {
    "model": "dall-e-2",
    "quality": "standard",
    "resolution": "1024x1024",
    "style": "vivid"
  },

  "speech": {
    "model": "tts-1",
    "voice": "alloy",
    "speed": 1,
    "format": "wav"
  },

  "transcription": {
    "model": "whisper-1",
    "prompt": "Transcribe the following audio file to German.",
    "language": "de",
    "temperature": 1
  }
}
```
Note: <br>
- Obtain your API key from [OpenAI](https://platform.openai.com/signup). <br>
- The `user` field is optional and can be used to identify the user for monitoring purposes. <br>
- Remove any section if you don't intend to use that service.

<hr>

### Chat Configuration

| **Field**             | **Description**                                                                 |
|:----------------------|:--------------------------------------------------------------------------------|
| model                 | Model used for generating text. See available models and their pricing below.   |
| maxConversationCalls  | Maximum number of calls per conversation.                                       |
| maxTokenSpendingLimit | Maximum tokens allowed per conversation.                                        |
| temperature           | Controls randomness: `0` (deterministic) to `2` (creative).                     |
| maxOutputTokens       | Maximum tokens in a response. So 500 characters are approximately 125 tokens).  |
| topP                  | Nucleus sampling: `0` (plain) to `1` (creative).                                |
| frequencyPenalty      | Reduces repetition of words. Values range from `0` to `1`.                      |
| presencePenalty       | Discourages repeating words from the conversation. Values range from `0` to `1` |
| instruction           | Provides guidance for the bot's behavior.                                       |

#### Chat Models and Pricing
| **Model**                                            | **Pricing**                                                                                    | **Max Output Tokens** |
|:-----------------------------------------------------|:-----------------------------------------------------------------------------------------------|:---------------------:|
| gpt-4o <br> gpt-4o-2024-11-20 <br> gpt-4o-2024-08-06 | $2.50 / 1M input tokens <br> \$1.25 / 1M cached input tokens <br> \$10.00 / 1M output tokens   |     16,384 tokens     |
| gpt-4o-2024-05-13                                    | $5.00 / 1M input tokens <br> \$15.00 / 1M output tokens                                        |     16,384 tokens     |
| chatgpt-4o-latest                                    | $5.00 / 1M input tokens <br> \$15.00 / 1M output tokens                                        |     4,096 tokens      |
| gpt-4o-mini <br> gpt-4o-mini-2024-07-18              | $0.150 / 1M input tokens <br> \$0.075 / 1M cached input tokens <br> \$0.600 / 1M output tokens |     16,384 tokens     |
| o1-preview <br> o1-preview-2024-09-12                | $15.00 / 1M input tokens <br> \$7.50 / 1M cached input tokens <br> \$60.00 / 1M output tokens  |     32,768 tokens     |
| o1-mini <br> o1-mini-2024-09-12                      | $3.00 / 1M input tokens <br> \$1.50 / 1M cached input tokens <br> \$12.00 / 1M output tokens   |     65,536 tokens     |

<hr>

### Image Configuration
| **Field**  | **Description**                                                          |
|:-----------|:-------------------------------------------------------------------------|
| model      | Model used for generating images (`dall-e-2`,` dall-e-3`).               |
| quality    | Image quality: `standard` or `hd` (only for `dall-e-3`).                 |
| resolution | Image size: `256x256`, `512x512`, `1024x1024`, `1024x1792`, `1792x1024`. |
| style      | Image style: `vivid` or `natural`. (only for `dall-e-3`)                 |

#### Image Models and Pricing
| **Model** | **Quality** | **Resolution**                        | **Pricing**                                                      |
|:----------|:-----------:|:--------------------------------------|:-----------------------------------------------------------------|
| dall-e-2  |             | 256x256 <br/> 512x512 <br/> 1024x1024 | \$0.016 per Image <br/> \$0.018 per Image <br/> $0.020 per Image |
| dall-e-3  |  standard   | 1024x1024 <br/> 1024x1792, 1792×1024  | \$0.040 per Image <br/> \$0.080 per Image                        |
| dall-e-3  |     hd      | 1024x1024 <br/> 1024x1792, 1792×1024  | \$0.080 per Image <br/> \$0.120 per Image                        |

<hr>

### Speech Configuration
| **Field** | **Description**                                                                               |
|:----------|:----------------------------------------------------------------------------------------------|
| model     | Speech model: `tts-1` or `tts-1-hd`.                                                          |
| voice     | Choose from voices: `alloy`, `echo`, `fable`, `onyx`, `nova`, `shimmer`.                      |
| format    | Audio file format: `mp3`, `opus`, `aac`, `flac`, `wav`, `pcm`. Currently only `wav` supported |
| speed     | Speech speed. Ranges from `0.25` (slowest) to `4` (fastest). Default is `1`.                  |

#### Speech Pricing
| **Model** | **Pricing**            | 
|:----------|:-----------------------|
| tts-1     | $15.00 / 1M characters |
| tts-1-hd  | $30.00 / 1M characters |

<hr>

### Transcription Configuration
| **Field**   | **Description**                                             |
|:------------|:------------------------------------------------------------|
| model       | Transcription model: `whisper-1`.                           |
| prompt      | Guidance prompt for transcription.                          |
| language    | Language of the audio (e.g., `en` for English).             |
| temperature | Controls randomness: `0` (deterministic) to `2` (creative). |

#### Transcription Pricing
| **Model** | **Pricing**                                     |
|:---------:|:------------------------------------------------|
| whisper-1 | $0.006 / minute (rounded to the nearest second) |


## Usage Example

```java
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
        chatExample(openAI);
        imageExample(openAI);
        AudioFile testFile = speechExample(openAI);
        transcriptionExample(openAI, testFile);
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
                "MCmoderSD",                                    // User
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
                "MCmoderSD",                                    // User
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
                "MCmoderSD",                // User
                "A cat, eating a donut",    // Prompt
                1,                          // Amount
                null,                       // Quality
                "256x256",                  // Resolution
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
                "This is a test recording",         // Text
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
```
For more examples, you can check the [YEPPTalk](https://github.com/MCmoderSD/YEPPTalk) project. <br>