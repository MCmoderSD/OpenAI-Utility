# OpenAI Utility
[![](https://jitpack.io/v/MCmoderSD/OpenAI-Utility.svg)](https://jitpack.io/#MCmoderSD/OpenAI-Utility)


## Description
This is a Java Utility for the OpenAI API Services. <br>
It uses the [OpenAI Library](https://github.com/TheoKanning/openai-java) from [TheoKanning](https://github.com/TheoKanning/) which is sadly not maintained anymore. <br>
The goal of this Utility is to provide a straightforward way to interact with the OpenAI API Services. <br>

Currently, it supports the Chat, Image, Speech and Transcription API services. <br>

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
You have to provide a JsonNode with the configuration for the Utility.<br>
The configuration should look like this:
```json
{
  "user": "YOUR_USERNAME",
  "apiKey": "YOUR_API_KEY",

  "chat": {
    "model": "gpt-4o-mini-2024-07-18",
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
    "language": "German",
    "temperature": 1
  }
}
```

You can get the API key from [OpenAI](https://platform.openai.com/signup). <br>
The **use
A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
If you don't want to use one of the services, you can remove the configuration for it. <br>

### Chat Configuration
- The **chatModel** is the model that the bot will use to generate the text. <br>
  The available models are: <br>

| **Model**              | **Pricing**                                                                                    | **Max Output Tokens** |
|:-----------------------|:-----------------------------------------------------------------------------------------------|:---------------------:|
| gpt-4o                 | $2.50 / 1M input tokens <br> \$1.25 / 1M cached input tokens <br> \$10.00 / 1M output tokens   |     16,384 tokens     |
| gpt-4o-2024-11-20      | $2.50 / 1M input tokens <br> \$1.25 / 1M cached input tokens <br> \$10.00 / 1M output tokens   |     16,384 tokens     |
| gpt-4o-2024-08-06      | $2.50 / 1M input tokens <br> \$1.25 / 1M cached input tokens <br> \$10.00 / 1M output tokens   |     16,384 tokens     |
| gpt-4o-2024-05-13      | $5.00 / 1M input tokens <br> \$15.00 / 1M output tokens                                        |     16,384 tokens     |
| chatgpt-4o-latest      | $5.00 / 1M input tokens <br> \$15.00 / 1M output tokens                                        |     4,096 tokens      |
| gpt-4o-mini            | $0.150 / 1M input tokens <br> \$0.075 / 1M cached input tokens <br> \$0.600 / 1M output tokens |     16,384 tokens     |
| gpt-4o-mini-2024-07-18 | $0.150 / 1M input tokens <br> \$0.075 / 1M cached input tokens <br> \$0.600 / 1M output tokens |     16,384 tokens     |
| o1-preview             | $15.00 / 1M input tokens <br> \$7.50 / 1M cached input tokens <br> \$60.00 / 1M output tokens  |     32,768 tokens     |
| o1-preview-2024-09-12  | $15.00 / 1M input tokens <br> \$7.50 / 1M cached input tokens <br> \$60.00 / 1M output tokens  |     32,768 tokens     |
| o1-mini                | $3.00 / 1M input tokens <br> \$1.50 / 1M cached input tokens <br> \$12.00 / 1M output tokens   |     65,536 tokens     |
| o1-mini-2024-09-12     | $3.00 / 1M input tokens <br> \$1.50 / 1M cached input tokens <br> \$12.00 / 1M output tokens   |     65,536 tokens     |

- The **maxConversationCalls** is the limit of calls per conversation. <br>
  After the limit is reached, the conversation will end. <br>


- The **maxTokenSpendingLimit** is the limit of tokens spent per conversation. <br>
  After the limit is reached, the conversation will end. <br>


- The **temperature** is the randomness of the text. <br>
  Lowering results in less random completions. As the temperature approaches zero, the model will become deterministic
  and repetitive. <br>
  Higher temperature results in more random completions. <br>
  The min value is 0 and the max value is 2. <br>


- The **maxOutputTokens** is the maximum length of the response text. <br>
  One token is roughly 4 characters for standard English text. <br>
  The limit is 16,384 tokens, but it's recommended to use a value that is suitable for the use, on Twitch the message limit is 500 characters.
  If you divide the limit by 4, you are an estimate the number of characters. <br>


- The **topP** is the nucleus sampling. <br>
  The lower the value, the more plain the text will be. <br>
  The higher the value, the more creative the text will be. <br>
  The min value is 0 and the max value is 1. <br>


- The **frequencyPenalty** reduces the likelihood of repeating the same words in a response.
  The higher the value, the less the bot will repeat itself. <br>
  The min value is 0 and the max value is 1. <br>


- The **presencePenalty** reduces the likelihood of mentioning words that have already appeared in the
  conversation. <br>
  The higher the value, the less the bot will repeat itself. <br>
  The min value is 0 and the max value is 1. <br>


- The **instruction** is the way the bot should behave and how he should reply to the prompt. <br>


### Image Configuration

- The **imageModel** is the model that the bot will use to generate the image. <br>
  The available models are: <br>

| **Model** | **Quality** | **Resolution**                        | **Pricing**                                                |
|:----------|:-----------:|:--------------------------------------|:-----------------------------------------------------------|
| dall-e-2  |             | 256x256 <br/> 512x512 <br/> 1024x1024 | \$0.016 / Image <br/> \$0.018 / Image <br/> $0.020 / Image |
| dall-e-3  |  standard   | 1024x1024 <br/> 1024x1792, 1792×1024  | \$0.040 / Image <br/> \$0.080 / Image                      |
| dall-e-3  |     hd      | 1024x1024 <br/> 1024x1792, 1792×1024  | \$0.080 / Image <br/> \$0.120 / Image                      |


- The **quality** is the quality of the image. <br>
  The available qualities are standard and hd. <br>
  The quality is only available for dall-e-3. <br>


- The **resolution** is the resolution of the image. <br>
  The available resolutions are 256x256, 512x512, 1024x1024, 1024x1792, and 1792x1024. <br>
  The resolution 1024x1024 is available for all models. <br>
  The resolution 256x256 and 512x512 are only available for dall-e-2. <br>
  The resolution 1024x1792 and 1792x1024 are only available for dall-e-3. <br>


- The **style** is the style of the image. <br>
  The available styles are vivid and natural. <br>
  The style is only available for dall-e-3. <br>
  The default style is vivid. <br>


### Speech Configuration

- The **ttsModel** is the model that the bot will use to generate the speech. <br>
  The available models are: <br>

| **Model** | **Pricing**            | 
|:----------|:-----------------------|
| tts-1     | $15.00 / 1M characters |
| tts-1-hd  | $30.00 / 1M characters |

- The **voice** is the voice that the bot will use to generate the speech. <br>
  The available voices are alloy, echo, fable, onyx, nova, and shimmer. <br>


- The **format** is the format of the audio file. <br>
  The available formats are mp3, opus, aac, flac, wav, and pcm. <br>


- The **speed** is the speed of the speech. <br>
  The min value is 0.25 and the max value is 4, the default value is 1. <br> <br>


### Transcription Configuration

- The **transcriptionModel** is the model that the bot will use to generate the transcription. <br>
  The available models are: <br>

| **Model** | **Pricing**                                     |
|:---------:|:------------------------------------------------|
| whisper-1 | $0.006 / minute (rounded to the nearest second) |


- The **prompt** is the prompt that the model will use to generate the transcription. <br>


- The **language** is the language of the audio. <br>


- The **temperature** is the randomness of the transcription. <br>
  Lowering results in less random completions. As the temperature approaches zero, the model will become deterministic
  and repetitive. <br>
  Higher temperature results in more random completions. <br>
  The min value is 0 and the max value is 2. <br> <br>

## Usage Example

```java
package de.MCmoderSD.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.theokanning.openai.image.ImageResult;

import de.MCmoderSD.OpenAI.OpenAI;
import de.MCmoderSD.OpenAI.enums.ImageModel;
import de.MCmoderSD.OpenAI.modules.Chat;
import de.MCmoderSD.OpenAI.modules.Image;
import de.MCmoderSD.OpenAI.modules.Speech;
import de.MCmoderSD.OpenAI.modules.Transcription;
import de.MCmoderSD.jal.AudioFile;

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
```

For more examples, you can check the [YEPPTalk](https://github.com/MCmoderSD/YEPPTalk) project. <br>