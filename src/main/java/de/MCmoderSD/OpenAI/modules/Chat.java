package de.MCmoderSD.OpenAI.modules;

import com.fasterxml.jackson.databind.JsonNode;

import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import de.MCmoderSD.OpenAI.enums.ChatModel;

import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The Chat class provides methods to interact with the OpenAI chat models.
 * It supports starting and continuing conversations, both synchronously and asynchronously.
 * The class also includes methods for validating parameters, calculating token usage, and computing costs.
 */
@SuppressWarnings({"ALL"})
public class Chat {

    // Constants
    public static final int CHAR_PER_TOKEN = 4;

    // Associations
    private final ChatModel model;
    private final OpenAiService service;

    // Attributes
    private final JsonNode config;
    private final String user;

    // Attributes
    private final HashMap<Integer, ArrayList<ChatMessage>> conversations;

    /**
     * Constructor for the Chat class.
     *
     * @param model   The chat model to use.
     * @param config  The configuration node for setting up parameters.
     * @param service The OpenAI service to communicate with.
     * @param user    The user initiating the chat.
     */
    public Chat(ChatModel model, JsonNode config, OpenAiService service, String user) {

        // Set Associations
        this.model = model;
        this.service = service;

        // Set Attributes
        this.config = config;
        this.user = user;

        // Init Attributes
        conversations = new HashMap<>();
    }

    /**
     * Retrieves the chat message from a chat completion result.
     *
     * @param result The chat completion result.
     * @return The chat message from the result.
     */
    public static ChatMessage getChatMessage(ChatCompletionResult result) {
        return result.getChoices().getFirst().getMessage();
    }

    /**
     * Extracts content from a chat completion chunk.
     *
     * @param chunk The chat completion chunk.
     * @return The content of the chunk, or an empty string if content is null.
     */
    public static String getContent(ChatCompletionChunk chunk) {
        if (chunk == null) return "";
        String content = chunk.getChoices().getFirst().getMessage().getContent();
        if (content == null) return "";
        else return content;
    }

    /**
     * Creates a new chat message.
     *
     * @param text   The text content of the message.
     * @param system Whether the message is from the system (true) or user (false).
     * @return A new chat message.
     */
    public static ChatMessage addMessage(String text, boolean system) {
        return new ChatMessage(system ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value(), text);
    }

    /**
     * Filters chat messages by role (system or user).
     *
     * @param messages The list of chat messages to filter.
     * @param system   Whether to filter for system messages (true) or user messages (false).
     * @return The filtered list of chat messages.
     */
    public static ArrayList<ChatMessage> filterMessages(ArrayList<ChatMessage> messages, boolean system) {
        ArrayList<ChatMessage> filtered = new ArrayList<>();
        for (ChatMessage message : messages) {
            String content = message.getContent();
            if (content == null || content.isBlank()) continue;
            if (message.getRole().equals(system ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value()))
                filtered.add(message);
        }
        return filtered;
    }

    /**
     * Calculates the number of tokens needed for a given text.
     *
     * @param text The text to calculate tokens for.
     * @return The number of tokens required.
     */
    public static int calculateTokens(String text) {
        return Math.ceilDiv(text.length(), CHAR_PER_TOKEN);
    }

    /**
     * Calculates the conversation limit based on token limit and max tokens per conversation.
     *
     * @param tokenLimit The token limit.
     * @param maxTokens  The maximum tokens per conversation.
     * @return The conversation limit.
     */
    public static int calculateConversationLimit(int tokenLimit, int maxTokens) {
        var conversationLimit = 0;
        var totalTokens = 0;
        while (totalTokens <= tokenLimit) {
            totalTokens += totalTokens + maxTokens * 2;
            if (totalTokens <= tokenLimit) conversationLimit++;
        }

        return conversationLimit;
    }

    /**
     * Calculates the total token spending limit based on conversation limit and max tokens per conversation.
     *
     * @param conversationLimit The conversation limit.
     * @param maxTokens         The maximum tokens per conversation.
     * @return The total token spending limit.
     */
    public static int calculateTokenSpendingLimit(int conversationLimit, int maxTokens) {
        var tokensUsed = 0;
        for (var i = 0; i < conversationLimit; i++) tokensUsed += tokensUsed + maxTokens * 2;
        return tokensUsed;
    }

    /**
     * Calculates the total tokens used by a list of chat messages.
     *
     * @param messages The list of chat messages.
     * @return The total number of tokens used.
     */
    public static int calculateTotalTokens(ArrayList<ChatMessage> messages) {

        // Variables
        var totalTokensUsed = 0;
        var totalTokens = 0;

        // Calculate tokens
        for (ChatMessage message : messages) {

            // Check content
            String content = message.getContent();
            if (content == null || content.isBlank()) continue;

            // Calculate tokens
            totalTokens += calculateTokens(content);
            if (messages.indexOf(message) > 1 && message.getRole().equals(ChatMessageRole.USER.value())) {
                totalTokensUsed += totalTokensUsed + totalTokens;
                totalTokens = 0;
            }
        }

        // Return total tokens
        return totalTokensUsed + totalTokens;
    }

    /**
     * Makes a chat completion request to the OpenAI service and returns the response.
     *
     * @param user             The user initiating the request.
     * @param messages         The chat history/messages.
     * @param temperature      The temperature setting for the response.
     * @param maxOutputTokens  The maximum number of output tokens.
     * @param topP             The topP parameter.
     * @param frequencyPenalty The frequency penalty parameter.
     * @param presencePenalty  The presence penalty parameter.
     * @return The chat message response.
     */
    private ChatMessage chatCompletionRequest(String user, ArrayList<ChatMessage> messages, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Request
        ChatCompletionRequest request = ChatCompletionRequest
                .builder()                              // Builder
                .model(model.getModel())                // Model
                .user(user)                             // User name
                .messages(messages)                     // Chat history
                .temperature(temperature)               // Temperature
                .maxTokens(maxOutputTokens)             // Max output tokens
                .topP(topP)                             // Top P
                .frequencyPenalty(frequencyPenalty)     // Frequency penalty
                .presencePenalty(presencePenalty)       // Presence penalty
                .n(1)                                // Number of completions
                .stream(false)                          // Stream
                .build();                               // Build

        // Result
        ChatCompletionResult result = service.createChatCompletion(request);
        return getChatMessage(result);
    }

    /**
     * Starts a conversation with the OpenAI service and returns the first response.
     *
     * @param id               The conversation ID.
     * @param user             The user initiating the conversation.
     * @param instruction      The instruction for the conversation.
     * @param message          The initial message for the conversation.
     * @param temperature      The temperature for the response.
     * @param maxOutputTokens  The maximum number of output tokens.
     * @param topP             The topP parameter.
     * @param frequencyPenalty The frequency penalty parameter.
     * @param presencePenalty  The presence penalty parameter.
     * @return The response from the conversation.
     */
    private ConnectableFlowable<ChatCompletionChunk> chatCompletionRequestStream(String user, ArrayList<ChatMessage> messages, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Request
        ChatCompletionRequest request = ChatCompletionRequest
                .builder()                              // Builder
                .model(model.getModel())                // Model
                .user(user)                             // User name
                .messages(messages)                     // Chat history
                .temperature(temperature)               // Temperature
                .maxTokens(maxOutputTokens)             // Max output tokens
                .topP(topP)                             // Top P
                .frequencyPenalty(frequencyPenalty)     // Frequency penalty
                .presencePenalty(presencePenalty)       // Presence penalty
                .n(1)                                // Number of completions
                .stream(true)                           // Stream
                .build();                               // Build

        return service.streamChatCompletion(request).publish();
    }

    /**
     * Starts a conversation with the OpenAI service and returns the first response.
     *
     * @param id               The conversation ID.
     * @param user             The user initiating the conversation.
     * @param instruction      The instruction for the conversation.
     * @param message          The initial message for the conversation.
     * @param temperature      The temperature for the response.
     * @param maxOutputTokens  The maximum number of output tokens.
     * @param topP             The topP parameter.
     * @param frequencyPenalty The frequency penalty parameter.
     * @param presencePenalty  The presence penalty parameter.
     * @return The response from the conversation.
     */
    private String startConversation(int id, String user, String instruction, String message, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Add Instruction
        conversations.put(id, new ArrayList<>(Collections.singleton(new ChatMessage(ChatMessageRole.SYSTEM.value(), instruction))));
        addMessage(id, message, false);

        // Get response
        ChatMessage response = chatCompletionRequest(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        addMessage(id, response);

        // Return response
        return response.getContent();
    }

    /**
     * Continues an existing conversation with the OpenAI service and returns the response.
     *
     * @param id               The conversation ID.
     * @param user             The user continuing the conversation.
     * @param message          The message to continue the conversation.
     * @param temperature      The temperature for the response.
     * @param maxOutputTokens  The maximum number of output tokens.
     * @param topP             The topP parameter.
     * @param frequencyPenalty The frequency penalty parameter.
     * @param presencePenalty  The presence penalty parameter.
     * @return The continued conversation response.
     */
    private String continueConversation(int id, String user, String message, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Add messages
        addMessage(id, message, false);

        // Get response
        ChatMessage response = chatCompletionRequest(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        addMessage(id, response);

        // Return response
        return response.getContent();
    }

    /**
     * Starts a conversation stream by initializing the conversation with an instruction
     * and sending a user message to the chat model. Returns a flowable that emits chunks
     * of the chat completion response.
     *
     * @param id               The unique identifier for the conversation.
     * @param user             The user initiating the conversation.
     * @param instruction      The instruction to guide the conversation.
     * @param message          The first message from the user to start the conversation.
     * @param temperature      The temperature setting for the response.
     * @param maxOutputTokens  The maximum number of tokens for the output.
     * @param topP             The topP parameter for the response.
     * @param frequencyPenalty The frequency penalty for the response.
     * @param presencePenalty  The presence penalty for the response.
     * @return A ConnectableFlowable emitting chat completion chunks as the conversation progresses.
     */
    private ConnectableFlowable<ChatCompletionChunk> startConversationStream(int id, String user, String instruction, String message, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Add Instruction
        conversations.put(id, new ArrayList<>(Collections.singleton(new ChatMessage(ChatMessageRole.SYSTEM.value(), instruction))));
        addMessage(id, message, false);

        // Get response
        ConnectableFlowable<ChatCompletionChunk> response = chatCompletionRequestStream(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        addMessage(id, response);

        // Get response
        return chatCompletionRequestStream(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
    }

    /**
     * Continues a conversation stream by adding a new user message and requesting a response
     * from the chat model. Returns a flowable that emits chunks of the chat completion response.
     *
     * @param id               The unique identifier for the conversation.
     * @param user             The user sending the message.
     * @param message          The new message from the user to continue the conversation.
     * @param temperature      The temperature setting for the response.
     * @param maxOutputTokens  The maximum number of tokens for the output.
     * @param topP             The topP parameter for the response.
     * @param frequencyPenalty The frequency penalty for the response.
     * @param presencePenalty  The presence penalty for the response.
     * @return A ConnectableFlowable emitting chat completion chunks as the conversation progresses.
     */
    private ConnectableFlowable<ChatCompletionChunk> continueConversationStream(int id, String user, String message, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Add messages
        addMessage(id, message, false);

        // Get response
        ConnectableFlowable<ChatCompletionChunk> response = chatCompletionRequestStream(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        addMessage(id, response);

        // Get response
        return chatCompletionRequestStream(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
    }

    /**
     * Validates the provided parameters for the chat conversation.
     * Checks the validity of the user name, instruction, prompt, and various model parameters.
     *
     * @param user             The user initiating the conversation.
     * @param instruction      The instruction for the conversation.
     * @param prompt           The prompt to be used in the conversation.
     * @param temperature      The temperature setting for the response.
     * @param maxOutputTokens  The maximum number of tokens for the output.
     * @param topP             The topP parameter for the response.
     * @param frequencyPenalty The frequency penalty for the response.
     * @param presencePenalty  The presence penalty for the response.
     * @return Returns `false` if any of the parameters are invalid, after throwing an exception.
     * @throws IllegalArgumentException if any parameter is invalid (null, empty, or out of bounds).
     */
    public boolean disprove(String user, String instruction, String prompt, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Check Username
        if (user == null) throw new IllegalArgumentException("User name is null");
        if (user.isBlank()) throw new IllegalArgumentException("User name is empty");

        // Check Instruction
        if (instruction == null) throw new IllegalArgumentException("Instruction is null");
        if (instruction.isBlank()) throw new IllegalArgumentException("Instruction is empty");

        // Check Prompt
        if (prompt == null) throw new IllegalArgumentException("Prompt is null");
        if (prompt.isBlank()) throw new IllegalArgumentException("Prompt is empty");

        // Check Variables
        if (!model.checkTemperature(temperature)) throw new IllegalArgumentException("Invalid temperature");
        if (!model.checkTokens(maxOutputTokens)) throw new IllegalArgumentException("Invalid max output tokens");
        if (!model.checkTopP((int) topP)) throw new IllegalArgumentException("Invalid top P");
        if (!model.checkFrequencyPenalty(frequencyPenalty))
            throw new IllegalArgumentException("Invalid frequency penalty");
        if (!model.checkPresencePenalty(presencePenalty))
            throw new IllegalArgumentException("Invalid presence penalty");

        // Disapprove parameters
        return false;
    }

    /**
     * Prompts the model with a user message and instruction, optionally allowing parameters to be set for
     * temperature, max output tokens, and other response settings. Returns the model's response.
     *
     * @param user             The user sending the prompt (optional).
     * @param instruction      The instruction to guide the response (optional).
     * @param prompt           The actual message or query for the model.
     * @param temperature      The temperature for the model's response (optional).
     * @param maxOutputTokens  The maximum number of tokens for the response (optional).
     * @param topP             The topP setting for response generation (optional).
     * @param frequencyPenalty The frequency penalty for response generation (optional).
     * @param presencePenalty  The presence penalty for response generation (optional).
     * @return The model's response as a string.
     * @throws IllegalArgumentException If any of the parameters are invalid.
     */
    public String prompt(@Nullable String user, @Nullable String instruction, String prompt, @Nullable Double temperature, @Nullable Integer maxOutputTokens, @Nullable Double topP, @Nullable Double frequencyPenalty, @Nullable Double presencePenalty) {

        // Approve parameters
        user = user == null ? this.user : user;
        instruction = instruction == null ? config.get("instruction").asText() : instruction;
        temperature = temperature == null ? config.get("temperature").asDouble() : temperature;
        maxOutputTokens = maxOutputTokens == null ? config.get("maxOutputTokens").asInt() : maxOutputTokens;
        topP = topP == null ? config.get("topP").asDouble() : topP;
        frequencyPenalty = frequencyPenalty == null ? config.get("frequencyPenalty").asDouble() : frequencyPenalty;
        presencePenalty = presencePenalty == null ? config.get("presencePenalty").asDouble() : presencePenalty;
        if (disprove(user, instruction, prompt, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty))
            throw new IllegalArgumentException("Invalid parameters");

        // Add messages
        ArrayList<ChatMessage> messages = new ArrayList<>();
        messages.add(addMessage(instruction, true));
        messages.add(addMessage(prompt, false));

        // Get response
        ChatMessage response = chatCompletionRequest(user, messages, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);

        // Return response
        return response.getContent();
    }

    /**
     * A simplified version of the prompt method where only the instruction and prompt are provided.
     *
     * @param instruction The instruction to guide the response.
     * @param prompt      The message or query for the model.
     * @return The model's response as a string.
     */
    public String prompt(String instruction, String prompt) {
        return prompt(null, instruction, prompt, null, null, null, null, null);
    }

    /**
     * A simplified version of the prompt method where only the prompt is provided, without instruction.
     *
     * @param prompt The message or query for the model.
     * @return The model's response as a string.
     */
    public String prompt(String prompt) {
        return prompt(null, null, prompt, null, null, null, null, null);
    }

    /**
     * Prompts the model with a message and instruction in a stream format, optionally allowing parameters
     * to be set for temperature, max output tokens, and other response settings.
     *
     * @param user             The user sending the prompt (optional).
     * @param instruction      The instruction to guide the response (optional).
     * @param prompt           The actual message or query for the model.
     * @param temperature      The temperature for the model's response (optional).
     * @param maxOutputTokens  The maximum number of tokens for the response (optional).
     * @param topP             The topP setting for response generation (optional).
     * @param frequencyPenalty The frequency penalty for response generation (optional).
     * @param presencePenalty  The presence penalty for response generation (optional).
     * @return A Flowable that emits chunks of the model's response.
     * @throws IllegalArgumentException If any of the parameters are invalid.
     */
    public Flowable<ChatCompletionChunk> promptStream(@Nullable String user, @Nullable String instruction, String prompt, @Nullable Double temperature, @Nullable Integer maxOutputTokens, @Nullable Double topP, @Nullable Double frequencyPenalty, @Nullable Double presencePenalty) {

        // Approve parameters
        user = user == null ? this.user : user;
        instruction = instruction == null ? config.get("instruction").asText() : instruction;
        temperature = temperature == null ? config.get("temperature").asDouble() : temperature;
        maxOutputTokens = maxOutputTokens == null ? config.get("maxOutputTokens").asInt() : maxOutputTokens;
        topP = topP == null ? config.get("topP").asDouble() : topP;
        frequencyPenalty = frequencyPenalty == null ? config.get("frequencyPenalty").asDouble() : frequencyPenalty;
        presencePenalty = presencePenalty == null ? config.get("presencePenalty").asDouble() : presencePenalty;
        if (disprove(user, instruction, prompt, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty))
            throw new IllegalArgumentException("Invalid parameters");

        // Add messages
        ArrayList<ChatMessage> messages = new ArrayList<>();
        messages.add(addMessage(instruction, true));
        messages.add(addMessage(prompt, false));

        // Get response
        return chatCompletionRequestStream(user, messages, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty).autoConnect();
    }

    /**
     * A simplified version of the promptStream method where only the instruction and prompt are provided.
     *
     * @param instruction The instruction to guide the response.
     * @param prompt      The message or query for the model.
     * @return A Flowable that emits chunks of the model's response.
     */
    public Flowable<ChatCompletionChunk> promptStream(String instruction, String prompt) {
        return promptStream(null, instruction, prompt, null, null, null, null, null);
    }

    /**
     * A simplified version of the promptStream method where only the prompt is provided.
     *
     * @param prompt The message or query for the model.
     * @return A Flowable that emits chunks of the model's response.
     */
    public Flowable<ChatCompletionChunk> promptStream(String prompt) {
        return promptStream(null, null, prompt, null, null, null, null, null);
    }

    /**
     * Starts a conversation or continues an existing conversation by sending a message and getting a response.
     * The conversation can be limited by max calls or max token spending.
     *
     * @param id                    The unique identifier for the conversation.
     * @param maxConversationCalls  The maximum number of calls allowed in the conversation (optional).
     * @param maxTokenSpendingLimit The maximum token limit for the conversation (optional).
     * @param user                  The user sending the message (optional).
     * @param instruction           The instruction to guide the response (optional).
     * @param message               The message to send.
     * @param temperature           The temperature for the response (optional).
     * @param maxOutputTokens       The maximum number of tokens for the response (optional).
     * @param topP                  The topP setting for the response (optional).
     * @param frequencyPenalty      The frequency penalty for the response (optional).
     * @param presencePenalty       The presence penalty for the response (optional).
     * @return The model's response as a string.
     * @throws IllegalArgumentException If any of the parameters are invalid.
     */
    public String converse(int id, @Nullable Integer maxConversationCalls, @Nullable Integer maxTokenSpendingLimit, @Nullable String user, @Nullable String instruction, String message, @Nullable Double temperature, @Nullable Integer maxOutputTokens, @Nullable Double topP, @Nullable Double frequencyPenalty, @Nullable Double presencePenalty) {

        // Approve parameters
        maxConversationCalls = maxConversationCalls == null ? config.get("maxConversationCalls").asInt() : maxConversationCalls;
        maxTokenSpendingLimit = maxTokenSpendingLimit == null ? config.get("maxTokenSpendingLimit").asInt() : maxTokenSpendingLimit;
        user = user == null ? this.user : user;
        instruction = instruction == null ? config.get("instruction").asText() : instruction;
        temperature = temperature == null ? config.get("temperature").asDouble() : temperature;
        maxOutputTokens = maxOutputTokens == null ? config.get("maxOutputTokens").asInt() : maxOutputTokens;
        topP = topP == null ? config.get("topP").asDouble() : topP;
        frequencyPenalty = frequencyPenalty == null ? config.get("frequencyPenalty").asDouble() : frequencyPenalty;
        presencePenalty = presencePenalty == null ? config.get("presencePenalty").asDouble() : presencePenalty;
        if (disprove(user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty))
            throw new IllegalArgumentException("Invalid parameters");

        // Variables
        ArrayList<ChatMessage> conversation;

        // Continue conversation
        if (hasConversation(id)) conversation = conversations.get(id);
        else
            return startConversation(id, user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);

        // Check Limit
        var contextValue = calculateTotalTokens(conversation) + calculateTokens(message) + maxOutputTokens;
        boolean tokenSpendingLimit = contextValue >= maxTokenSpendingLimit;
        boolean conversationLimit = filterMessages(conversation, false).size() >= maxConversationCalls;

        // Continue conversation
        if (!(tokenSpendingLimit || conversationLimit))
            return continueConversation(id, user, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        else conversations.remove(id);

        // Return message
        if (conversationLimit)
            return "The conversation has reached the call limit of " + maxConversationCalls + " calls";
        else return "The conversation has reached the token limit of " + maxTokenSpendingLimit + " tokens";
    }

    /**
     * A simplified version of the converse method where only the instruction and message are provided.
     *
     * @param id          The unique identifier for the conversation.
     * @param instruction The instruction to guide the response.
     * @param message     The message to send.
     * @return The model's response as a string.
     */
    public String converse(int id, String instruction, String message) {
        return converse(id, null, null, null, instruction, message, null, null, null, null, null);
    }

    /**
     * A simplified version of the converse method where only the message is provided.
     *
     * @param id      The unique identifier for the conversation.
     * @param message The message to send.
     * @return The model's response as a string.
     */
    public String converse(int id, String message) {
        return converse(id, null, null, null, null, message, null, null, null, null, null);
    }

    /**
     * Starts or continues a conversation with a stream of messages, emitting response chunks.
     *
     * @param id                    The unique identifier for the conversation.
     * @param maxConversationCalls  The maximum number of calls allowed in the conversation (optional).
     * @param maxTokenSpendingLimit The maximum token limit for the conversation (optional).
     * @param user                  The user sending the message (optional).
     * @param instruction           The instruction to guide the response (optional).
     * @param message               The message to send.
     * @param temperature           The temperature for the response (optional).
     * @param maxOutputTokens       The maximum number of tokens for the response (optional).
     * @param topP                  The topP setting for the response (optional).
     * @param frequencyPenalty      The frequency penalty for the response (optional).
     * @param presencePenalty       The presence penalty for the response (optional).
     * @return A Flowable that emits chunks of the model's response.
     * @throws IllegalArgumentException If any of the parameters are invalid.
     */
    public Flowable<ChatCompletionChunk> converseStream(int id, @Nullable Integer maxConversationCalls, @Nullable Integer maxTokenSpendingLimit, @Nullable String user, @Nullable String instruction, String message, @Nullable Double temperature, @Nullable Integer maxOutputTokens, @Nullable Double topP, @Nullable Double frequencyPenalty, @Nullable Double presencePenalty) {

        // Approve parameters
        maxConversationCalls = maxConversationCalls == null ? config.get("maxConversationCalls").asInt() : maxConversationCalls;
        maxTokenSpendingLimit = maxTokenSpendingLimit == null ? config.get("maxTokenSpendingLimit").asInt() : maxTokenSpendingLimit;
        user = user == null ? this.user : user;
        instruction = instruction == null ? config.get("instruction").asText() : instruction;
        temperature = temperature == null ? config.get("temperature").asDouble() : temperature;
        maxOutputTokens = maxOutputTokens == null ? config.get("maxOutputTokens").asInt() : maxOutputTokens;
        topP = topP == null ? config.get("topP").asDouble() : topP;
        frequencyPenalty = frequencyPenalty == null ? config.get("frequencyPenalty").asDouble() : frequencyPenalty;
        presencePenalty = presencePenalty == null ? config.get("presencePenalty").asDouble() : presencePenalty;
        if (disprove(user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty))
            throw new IllegalArgumentException("Invalid parameters");

        // Variables
        ArrayList<ChatMessage> conversation;

        // Continue conversation
        if (hasConversation(id)) conversation = conversations.get(id);
        else
            return startConversationStream(id, user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);

        // Check Limit
        var contextValue = calculateTotalTokens(conversation) + calculateTokens(message) + maxOutputTokens;
        boolean tokenSpendingLimit = contextValue >= maxTokenSpendingLimit;
        boolean conversationLimit = filterMessages(conversation, false).size() >= maxConversationCalls;

        // Continue conversation
        if (!(tokenSpendingLimit || conversationLimit))
            return continueConversationStream(id, user, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty).autoConnect();
        else conversations.remove(id);
        return null;
    }

    /**
     * A simplified version of the converseStream method where only the instruction and message are provided.
     *
     * @param id          The unique identifier for the conversation.
     * @param instruction The instruction to guide the response.
     * @param message     The message to send.
     * @return A Flowable that emits chunks of the model's response.
     */
    public Flowable<ChatCompletionChunk> converseStream(int id, String instruction, String message) {
        return converseStream(id, null, null, null, instruction, message, null, null, null, null, null);
    }

    /**
     * A simplified version of the converseStream method where only the message is provided.
     *
     * @param id      The unique identifier for the conversation.
     * @param message The message to send.
     * @return A Flowable that emits chunks of the model's response.
     */
    public Flowable<ChatCompletionChunk> converseStream(int id, String message) {
        return converseStream(id, null, null, null, null, message, null, null, null, null, null);
    }

    /**
     * Adds a message to the conversation with the specified ID.
     *
     * @param id      The ID of the conversation.
     * @param message The message to add to the conversation.
     */
    private void addMessage(int id, ChatMessage message) {
        conversations.get(id).add(message);
    }

    /**
     * Adds a system or user message to the conversation with the specified ID.
     *
     * @param id     The ID of the conversation.
     * @param text   The message text to add.
     * @param system If true, the message is added as a system message; otherwise, it is a user message.
     */
    private void addMessage(int id, String text, boolean system) {
        conversations.get(id).add(new ChatMessage(system ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value(), text));
    }

    /**
     * Adds a streamed message (from a ConnectableFlowable) to the conversation with the specified ID.
     *
     * @param id   The ID of the conversation.
     * @param text The streamed message to add.
     */
    private void addMessage(int id, ConnectableFlowable<ChatCompletionChunk> text) {
        StringBuilder message = new StringBuilder();
        text.subscribe(chunk -> message.append(getContent(chunk)));
        addMessage(id, message.toString(), true);
    }

    /**
     * Retrieves the current configuration settings.
     *
     * @return The configuration as a JsonNode object.
     */
    public JsonNode getConfig() {
        return config;
    }

    /**
     * Retrieves the model associated with the service.
     *
     * @return The ChatModel instance.
     */
    public ChatModel getModel() {
        return model;
    }

    /**
     * Retrieves the service instance being used.
     *
     * @return The OpenAiService instance.
     */
    public OpenAiService getService() {
        return service;
    }

    /**
     * Checks if a conversation exists for the specified ID.
     *
     * @param id The ID of the conversation.
     * @return True if the conversation exists, otherwise false.
     */
    public boolean hasConversation(int id) {
        return conversations.containsKey(id);
    }

    /**
     * Retrieves the size (number of messages) in the conversation with the specified ID.
     *
     * @param id The ID of the conversation.
     * @return The number of messages in the conversation.
     */
    public int getConversationSize(int id) {
        return conversations.get(id).size();
    }

    /**
     * Retrieves the total number of tokens used in the conversation with the specified ID.
     *
     * @param id The ID of the conversation.
     * @return The total number of tokens in the conversation.
     */
    public int getConversationTokens(int id) {
        return calculateTotalTokens(conversations.get(id));
    }

    /**
     * Clears all conversations from the system.
     */
    public void clearConversations() {
        conversations.clear();
    }

    /**
     * Clears the conversation with the specified ID.
     *
     * @param id The ID of the conversation to clear.
     */
    public void clearConversation(int id) {
        conversations.remove(id);
    }

    /**
     * Calculates the cost of a prompt based on the input and output texts.
     *
     * @param inputText  The input text for the prompt.
     * @param outputText The output text from the model.
     * @return The calculated cost of the prompt.
     */
    public BigDecimal calculatePromptCost(String inputText, String outputText) {
        return model.calculateCost(calculateTokens(inputText), calculateTokens(outputText));
    }

    /**
     * Calculates the cost of a prompt based on the provided text.
     *
     * @param text The text for the prompt.
     * @return The calculated cost of the prompt.
     */
    public BigDecimal calculatePromptCost(String text) {
        return model.calculateCost(calculateTokens(text));
    }

    /**
     * Calculates the cost of a prompt based on the number of input and output tokens.
     *
     * @param inputTokens  The number of input tokens.
     * @param outputTokens The number of output tokens.
     * @return The calculated cost of the prompt.
     */
    public BigDecimal calculatePromptCost(int inputTokens, int outputTokens) {
        return model.calculateCost(inputTokens, outputTokens);
    }

    /**
     * Calculates the cost of a prompt based on the total number of tokens.
     *
     * @param tokens The total number of tokens.
     * @return The calculated cost of the prompt.
     */
    public BigDecimal calculatePromptCost(int tokens) {
        return model.calculateCost(tokens);
    }

    /**
     * Calculates the cost of a conversation based on the conversation ID.
     *
     * @param id The ID of the conversation.
     * @return The calculated cost of the conversation.
     */
    public BigDecimal calculateConversationCost(int id) {
        return model.calculateCost(getConversationTokens(id));
    }
}