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

    // Static Methods
    public static ChatMessage getChatMessage(ChatCompletionResult result) {
        return result.getChoices().getFirst().getMessage();
    }

    public static String getContent(ChatCompletionChunk chunk) {
        if (chunk == null) return "";
        String content = chunk.getChoices().getFirst().getMessage().getContent();
        if (content == null) return "";
        else return content;
    }

    public static ChatMessage addMessage(String text, boolean system) {
        return new ChatMessage(system ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value(), text);
    }

    public static ArrayList<ChatMessage> filterMessages(ArrayList<ChatMessage> messages, boolean system) {
        ArrayList<ChatMessage> filtered = new ArrayList<>();
        for (ChatMessage message : messages) {
            String content = message.getContent();
            if (content == null || content.isBlank()) continue;
            if (message.getRole().equals(system ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value())) filtered.add(message);
        }
        return filtered;
    }

    public static int calculateTokens(String text) {
        return Math.ceilDiv(text.length(), CHAR_PER_TOKEN);
    }

    public static int calculateConversationLimit(int tokenLimit, int maxTokens) {
        var conversationLimit = 0;
        var totalTokens = 0;
        while (totalTokens <= tokenLimit) {
            totalTokens += totalTokens + maxTokens * 2;
            if (totalTokens <= tokenLimit) conversationLimit++;
        }

        return conversationLimit;
    }

    public static int calculateTokenSpendingLimit(int conversationLimit, int maxTokens) {
        var tokensUsed = 0;
        for (var i = 0; i < conversationLimit; i++) tokensUsed += tokensUsed + maxTokens * 2;
        return tokensUsed;
    }

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

    // Create Chat
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

    // Create Chat Stream
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

    // Check Parameters
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
        if (!model.checkFrequencyPenalty(frequencyPenalty)) throw new IllegalArgumentException("Invalid frequency penalty");
        if (!model.checkPresencePenalty(presencePenalty)) throw new IllegalArgumentException("Invalid presence penalty");

        // Disapprove parameters
        return false;
    }

    // Start Conversation
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

    // Continue Conversation
    private String continueConversation(int id, String user, String message, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Add messages
        addMessage(id, message, false);

        // Get response
        ChatMessage response = chatCompletionRequest(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        addMessage(id, response);

        // Return response
        return response.getContent();
    }

    // Start Conversation Stream
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

    // Continue Conversation Stream
    private ConnectableFlowable<ChatCompletionChunk> continueConversationStream(int id, String user, String message, double temperature, int maxOutputTokens, double topP, double frequencyPenalty, double presencePenalty) {

        // Add messages
        addMessage(id, message, false);

        // Get response
        ConnectableFlowable<ChatCompletionChunk> response = chatCompletionRequestStream(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        addMessage(id, response);

        // Get response
        return chatCompletionRequestStream(user, conversations.get(id), temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
    }

    // Prompt
    public String prompt(@Nullable String user, @Nullable String instruction, String prompt, @Nullable Double temperature, @Nullable Integer maxOutputTokens, @Nullable Double topP, @Nullable Double frequencyPenalty, @Nullable Double presencePenalty) {

        // Approve parameters
        user = user == null ? this.user : user;
        instruction = instruction == null ? config.get("instruction").asText() : instruction;
        temperature = temperature == null ? config.get("temperature").asDouble() : temperature;
        maxOutputTokens = maxOutputTokens == null ? config.get("maxOutputTokens").asInt() : maxOutputTokens;
        topP = topP == null ? config.get("topP").asDouble() : topP;
        frequencyPenalty = frequencyPenalty == null ? config.get("frequencyPenalty").asDouble() : frequencyPenalty;
        presencePenalty = presencePenalty == null ? config.get("presencePenalty").asDouble() : presencePenalty;
        if (disprove(user, instruction, prompt, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty)) throw new IllegalArgumentException("Invalid parameters");

        // Add messages
        ArrayList<ChatMessage> messages = new ArrayList<>();
        messages.add(addMessage(instruction, true));
        messages.add(addMessage(prompt, false));

        // Get response
        ChatMessage response = chatCompletionRequest(user, messages, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);

        // Return response
        return response.getContent();
    }

    public String prompt(String instruction, String prompt) {
        return prompt(null, instruction, prompt, null, null, null, null, null);
    }

    public String prompt(String prompt) {
        return prompt(null, null, prompt, null, null, null, null, null);
    }

    // Prompt Stream
    public Flowable<ChatCompletionChunk> promptStream(@Nullable String user, @Nullable String instruction, String prompt, @Nullable Double temperature, @Nullable Integer maxOutputTokens, @Nullable Double topP, @Nullable Double frequencyPenalty, @Nullable Double presencePenalty) {

        // Approve parameters
        user = user == null ? this.user : user;
        instruction = instruction == null ? config.get("instruction").asText() : instruction;
        temperature = temperature == null ? config.get("temperature").asDouble() : temperature;
        maxOutputTokens = maxOutputTokens == null ? config.get("maxOutputTokens").asInt() : maxOutputTokens;
        topP = topP == null ? config.get("topP").asDouble() : topP;
        frequencyPenalty = frequencyPenalty == null ? config.get("frequencyPenalty").asDouble() : frequencyPenalty;
        presencePenalty = presencePenalty == null ? config.get("presencePenalty").asDouble() : presencePenalty;
        if (disprove(user, instruction, prompt, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty)) throw new IllegalArgumentException("Invalid parameters");

        // Add messages
        ArrayList<ChatMessage> messages = new ArrayList<>();
        messages.add(addMessage(instruction, true));
        messages.add(addMessage(prompt, false));

        // Get response
        return chatCompletionRequestStream(user, messages, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty).autoConnect();
    }

    public Flowable<ChatCompletionChunk> promptStream(String instruction, String prompt) {
        return promptStream(null, instruction, prompt, null, null, null, null, null);
    }

    public Flowable<ChatCompletionChunk> promptStream(String prompt) {
        return promptStream(null, null, prompt, null, null, null, null, null);
    }

    // Conversation
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
        if (disprove(user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty)) throw new IllegalArgumentException("Invalid parameters");

        // Variables
        ArrayList<ChatMessage> conversation;

        // Continue conversation
        if (hasConversation(id)) conversation = conversations.get(id);
        else return startConversation(id, user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);

        // Check Limit
        var contextValue = calculateTotalTokens(conversation) + calculateTokens(message) + maxOutputTokens;
        boolean tokenSpendingLimit = contextValue >= maxTokenSpendingLimit;
        boolean conversationLimit = filterMessages(conversation, false).size() >= maxConversationCalls;

        // Continue conversation
        if (!(tokenSpendingLimit || conversationLimit)) return continueConversation(id, user, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);
        else conversations.remove(id);

        // Return message
        if (conversationLimit) return "The conversation has reached the call limit of " + maxConversationCalls + " calls";
        else return "The conversation has reached the token limit of " + maxTokenSpendingLimit + " tokens";
    }

    public String converse(int id, String instruction, String message) {
        return converse(id, null, null, null, instruction, message, null, null, null, null, null);
    }

    public String converse(int id, String message) {
        return converse(id, null, null, null, null, message, null, null, null, null, null);
    }

    // Conversation Stream
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
        if (disprove(user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty)) throw new IllegalArgumentException("Invalid parameters");

        // Variables
        ArrayList<ChatMessage> conversation;

        // Continue conversation
        if (hasConversation(id)) conversation = conversations.get(id);
        else return startConversationStream(id, user, instruction, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty);

        // Check Limit
        var contextValue = calculateTotalTokens(conversation) + calculateTokens(message) + maxOutputTokens;
        boolean tokenSpendingLimit = contextValue >= maxTokenSpendingLimit;
        boolean conversationLimit = filterMessages(conversation, false).size() >= maxConversationCalls;

        // Continue conversation
        if (!(tokenSpendingLimit || conversationLimit)) return continueConversationStream(id, user, message, temperature, maxOutputTokens, topP, frequencyPenalty, presencePenalty).autoConnect();
        else conversations.remove(id);
        return null;
    }

    public Flowable<ChatCompletionChunk> converseStream(int id, String instruction, String message) {
        return converseStream(id, null, null, null, instruction, message, null, null, null, null, null);
    }

    public Flowable<ChatCompletionChunk> converseStream(int id, String message) {
        return converseStream(id, null, null, null, null, message, null, null, null, null, null);
    }

    // Add Message
    private void addMessage(int id, ChatMessage message) {
        conversations.get(id).add(message);
    }

    private void addMessage(int id, String text, boolean system) {
        conversations.get(id).add(new ChatMessage(system ? ChatMessageRole.SYSTEM.value() : ChatMessageRole.USER.value(), text));
    }

    private void addMessage(int id, ConnectableFlowable<ChatCompletionChunk> text) {
        StringBuilder message = new StringBuilder();
        text.subscribe(chunk -> message.append(getContent(chunk)));
        addMessage(id, message.toString(), true);
    }

    // Getter
    public JsonNode getConfig() {
        return config;
    }

    public ChatModel getModel() {
        return model;
    }

    public OpenAiService getService() {
        return service;
    }

    public boolean hasConversation(int id) {
        return conversations.containsKey(id);
    }

    public int getConversationSize(int id) {
        return conversations.get(id).size();
    }

    public int getConversationTokens(int id) {
        return calculateTotalTokens(conversations.get(id));
    }

    // Setter
    public void clearConversations() {
        conversations.clear();
    }

    public void clearConversation(int id) {
        conversations.remove(id);
    }

    // Calculate
    public BigDecimal calculatePromptCost(String inputText, String outputText) {
        return model.calculateCost(calculateTokens(inputText), calculateTokens(outputText));
    }

    public BigDecimal calculatePromptCost(String text) {
        return model.calculateCost(calculateTokens(text));
    }

    public BigDecimal calculatePromptCost(int inputTokens, int outputTokens) {
        return model.calculateCost(inputTokens, outputTokens);
    }

    public BigDecimal calculatePromptCost(int tokens) {
        return model.calculateCost(tokens);
    }

    public BigDecimal calculateConversationCost(int id) {
        return model.calculateCost(getConversationTokens(id));
    }
}