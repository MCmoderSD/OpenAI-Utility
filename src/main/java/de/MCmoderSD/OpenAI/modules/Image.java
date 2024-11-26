package de.MCmoderSD.OpenAI.modules;

import com.fasterxml.jackson.databind.JsonNode;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;

import de.MCmoderSD.OpenAI.enums.ImageModel;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashSet;

@SuppressWarnings({"ALL"})
public class Image {

    // Associations
    private final ImageModel model;
    private final OpenAiService service;

    // Attributes
    private final JsonNode config;
    private final String user;

    // Constructor
    public Image(ImageModel model, JsonNode config, OpenAiService service, String user) {

        // Set Associations
        this.model = model;
        this.service = service;

        // Set Attributes
        this.config = config;
        this.user = user;
    }

    // Create Speech
    private ImageResult imageRequest(String user, String prompt, int amount, String quality, String resolution, String style) {

        // Request
        CreateImageRequest request = CreateImageRequest
                .builder()                  // Builder
                .model(model.getModel())    // Model
                .user(user)                 // User
                .prompt(prompt)             // Prompt
                .n(amount)                  // Amount
                .quality(quality)           // Quality
                .size(resolution)           // Resolution
                .style(style)               // Style
                .build();                   // Build

        // Result
        return service.createImage(request);
    }

    // Check Parameters
    public boolean disprove(String user, String prompt, Integer amount, String quality, String resolution, String style) {

        // Check Username
        if (user == null) throw new IllegalArgumentException("User name is null");
        if (user.isBlank()) throw new IllegalArgumentException("User name is empty");

        // Check Prompt
        if (prompt == null) throw new IllegalArgumentException("Prompt is null");
        if (prompt.isBlank()) throw new IllegalArgumentException("Prompt is empty");

        // Check Variables
        if (!model.checkPrompt(prompt)) throw new IllegalArgumentException("Invalid prompt");
        if (!model.checkAmount(amount)) throw new IllegalArgumentException("Invalid amount");
        if (!model.checkQuality(quality)) throw new IllegalArgumentException("Invalid quality");
        if (!model.checkResolution(resolution)) throw new IllegalArgumentException("Invalid resolution");
        if (!model.checkStyle(style)) throw new IllegalArgumentException("Invalid style");

        // Disapprove parameters
        return false;
    }

    // Create Image
    public HashSet<String> generate(@Nullable String user, String prompt, @Nullable Integer amount, @Nullable String quality, @Nullable String resolution, @Nullable String style) {

        // Approve parameters
        user = user == null ? this.user : user;
        amount = amount == null ? 1 : amount;
        quality = quality == null ? config.get("quality").asText() : quality;
        resolution = resolution == null ? config.get("resolution").asText() : resolution;
        style = style == null ? config.get("style").asText() : style;
        if (disprove(user, prompt, amount, quality, resolution, style)) throw new IllegalArgumentException("Invalid parameters");

        // Request Image
        ImageResult result = imageRequest(user, prompt, amount, quality, resolution, style);

        // Return Images
        HashSet<String> images = new HashSet<>();
        result.getData().forEach(image -> images.add(image.getUrl()));
        return images;
    }

    public HashSet<String> generate(String prompt) {
        return generate(null, prompt, null, null, null, null);
    }

    // Getter
    public JsonNode getConfig() {
        return config;
    }

    public ImageModel getModel() {
        return model;
    }

    public OpenAiService getService() {
        return service;
    }

    public BigDecimal calculatePrice(ImageModel.Resolution resolution, ImageModel.Quality quality) {
        return model.getPrice(resolution, quality);
    }
}
