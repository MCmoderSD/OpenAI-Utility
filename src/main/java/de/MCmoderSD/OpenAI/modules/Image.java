package de.MCmoderSD.OpenAI.modules;

import com.fasterxml.jackson.databind.JsonNode;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;

import de.MCmoderSD.OpenAI.enums.ImageModel;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * The Image class is responsible for interacting with OpenAI's image generation models (e.g., DALL·E 2 and DALL·E 3).
 * It handles the creation of image requests, validation of parameters, and calculation of image generation costs.
 * The class supports both image generation and validation for various image generation parameters like resolution,
 * quality, style, and amount.
 */
@SuppressWarnings({"ALL"})
public class Image {

    // Associations
    private final ImageModel model;
    private final OpenAiService service;

    // Attributes
    private final JsonNode config;
    private final String user;

    /**
     * Constructor to initialize the Image object with the necessary associations and attributes.
     *
     * @param model  The image model to use for generation (e.g., DALL·E 2, DALL·E 3).
     * @param config Configuration settings (e.g., quality, resolution, style).
     * @param service The OpenAiService instance to interact with the OpenAI API.
     * @param user   The user associated with the image generation.
     */
    public Image(ImageModel model, JsonNode config, OpenAiService service, String user) {

        // Set Associations
        this.model = model;
        this.service = service;

        // Set Attributes
        this.config = config;
        this.user = user;
    }

    /**
     * Creates an image request using the provided parameters (for DALL·E 3).
     *
     * @param user      The user making the request.
     * @param prompt    The prompt describing the image.
     * @param quality   The quality of the image.
     * @param resolution The resolution of the image.
     * @param style     The style of the image.
     * @return The result containing the generated image.
     */
    private ImageResult imageRequest(String user, String prompt, String quality, String resolution, String style) {

        // Request
        CreateImageRequest request = CreateImageRequest
                .builder()                              // Builder
                .model(ImageModel.DALL_E_3.getModel())  // Model
                .user(user)                             // User
                .prompt(prompt)                         // Prompt
                .quality(quality)                       // Quality
                .size(resolution)                       // Resolution
                .style(style)                           // Style
                .build();                               // Build

        // Result
        return service.createImage(request);
    }

    /**
     * Creates an image request using the provided parameters (for DALL·E 2).
     *
     * @param user      The user making the request.
     * @param prompt    The prompt describing the image.
     * @param amount    The number of images to generate.
     * @param resolution The resolution of the image.
     * @return The result containing the generated image(s).
     */
    private ImageResult imageRequest(String user, String prompt, int amount, String resolution) {
        // Request
        CreateImageRequest request = CreateImageRequest
                .builder()                              // Builder
                .model(ImageModel.DALL_E_2.getModel())  // Model
                .user(user)                             // User
                .prompt(prompt)                         // Prompt
                .n(amount)                              // Amount
                .size(resolution)                       // Resolution
                .build();                               // Build

        // Result
        return service.createImage(request);
    }

    /**
     * Validates the parameters for image generation (for DALL·E 3 model).
     *
     * @param user      The user making the request.
     * @param prompt    The prompt describing the image.
     * @param quality   The quality of the image.
     * @param resolution The resolution of the image.
     * @param style     The style of the image.
     * @return True if the parameters are valid, false otherwise.
     */
    public boolean disprove(String user, String prompt, String quality, String resolution, String style) {

        // Check Username
        if (user == null) throw new IllegalArgumentException("User name is null");
        if (user.isBlank()) throw new IllegalArgumentException("User name is empty");

        // Check Prompt
        if (prompt == null) throw new IllegalArgumentException("Prompt is null");
        if (prompt.isBlank()) throw new IllegalArgumentException("Prompt is empty");

        // Check Variables
        if (!model.checkPrompt(prompt)) throw new IllegalArgumentException("Invalid prompt");
        if (!model.checkQuality(quality)) throw new IllegalArgumentException("Invalid quality");
        if (!model.checkResolution(resolution)) throw new IllegalArgumentException("Invalid resolution");
        if (!model.checkStyle(style)) throw new IllegalArgumentException("Invalid style");

        // Disapprove parameters
        return false;
    }

    /**
     * Validates the parameters for image generation (for DALL·E 2 model).
     *
     * @param user      The user making the request.
     * @param prompt    The prompt describing the image.
     * @param amount    The number of images to generate.
     * @param resolution The resolution of the image.
     * @return True if the parameters are valid, false otherwise.
     */
    public boolean disprove(String user, String prompt, int amount, String resolution) {

        // Check Username
        if (user == null) throw new IllegalArgumentException("User name is null");
        if (user.isBlank()) throw new IllegalArgumentException("User name is empty");

        // Check Prompt
        if (prompt == null) throw new IllegalArgumentException("Prompt is null");
        if (prompt.isBlank()) throw new IllegalArgumentException("Prompt is empty");

        // Check Variables
        if (!model.checkPrompt(prompt)) throw new IllegalArgumentException("Invalid prompt");
        if (!model.checkAmount(amount)) throw new IllegalArgumentException("Invalid amount");
        if (!model.checkResolution(resolution)) throw new IllegalArgumentException("Invalid resolution");

        // Disapprove parameters
        return false;
    }

    /**
     * Generates images based on the provided parameters, with validation and defaults for missing values.
     *
     * @param user       The user making the request (optional).
     * @param prompt     The prompt describing the image.
     * @param amount     The number of images to generate (optional).
     * @param quality    The quality of the image (optional).
     * @param resolution The resolution of the image (optional).
     * @param style      The style of the image (optional).
     * @return A set of image URLs.
     */
    public HashSet<String> generate(@Nullable String user, String prompt, @Nullable Integer amount, @Nullable String quality, @Nullable String resolution, @Nullable String style) {

        // Approve parameters
        user = user == null ? this.user : user;
        amount = amount == null ? 1 : amount;
        quality = quality == null ? config.get("quality").asText() : quality;
        resolution = resolution == null ? config.get("resolution").asText() : resolution;
        style = style == null ? config.get("style").asText() : style;
        if (model.equals(ImageModel.DALL_E_3)) {
            if (disprove(user, prompt, quality, resolution, style)) throw new IllegalArgumentException("Invalid parameters");
        } else if (disprove(user, prompt, amount, resolution)) throw new IllegalArgumentException("Invalid parameters");

        // Request Image
        ImageResult result = model.equals(ImageModel.DALL_E_3) ? imageRequest(user, prompt, quality, resolution, style) : imageRequest(user, prompt, amount, resolution);

        // Return Images
        HashSet<String> images = new HashSet<>();
        result.getData().forEach(image -> images.add(image.getUrl()));
        return images;
    }
    /**
     * Generates an image based on the provided prompt using default values for other parameters.
     *
     * @param prompt The prompt describing the image.
     * @return A set of image URLs.
     */
    public HashSet<String> generate(String prompt) {
        return generate(null, prompt, null, null, null, null);
    }

    /**
     * Retrieves the configuration settings for image generation.
     *
     * @return The configuration as a JsonNode object.
     */
    public JsonNode getConfig() {
        return config;
    }

    /**
     * Retrieves the image model associated with this instance.
     *
     * @return The ImageModel.
     */
    public ImageModel getModel() {
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
     * Calculates the price of generating an image based on the resolution and quality.
     *
     * @param resolution The resolution of the image.
     * @param quality    The quality of the image.
     * @return The price as a BigDecimal.
     */
    public BigDecimal calculatePrice(ImageModel.Resolution resolution, ImageModel.Quality quality) {
        return model.getPrice(resolution, quality);
    }
}