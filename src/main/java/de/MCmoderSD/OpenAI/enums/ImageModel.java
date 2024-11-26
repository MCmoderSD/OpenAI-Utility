package de.MCmoderSD.OpenAI.enums;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;

/**
 * Represents different image models with their associated attributes, such as supported resolutions,
 * styles, and pricing details.
 */
@SuppressWarnings("ALL")
public enum ImageModel {

    /**
     * DALL-E 2 model, supporting resolutions up to 1024x1024.
     */
    DALL_E_2("dall-e-2", Resolution.RES_256x256, Resolution.RES_512x512, Resolution.RES_1024x1024),

    /**
     * DALL-E 3 model, supporting advanced resolutions and additional styles.
     */
    DALL_E_3("dall-e-3", Resolution.RES_1024x1024, Resolution.RES_1024x1792, Resolution.RES_1792x1024);

    // Attributes
    private final HashSet<String> models;
    private final HashSet<Resolution> resolutions;
    private final int minCharacters;
    private final int maxCharacters;
    private final int minAmount;
    private final int maxAmount;
    private final HashSet<Style> style;
    private final String model;

    /**
     * Constructs an {@code ImageModel} with the specified attributes.
     *
     * @param model      the model name (e.g., "dall-e-2", "dall-e-3")
     * @param resolutions the resolutions supported by the model
     * @throws IllegalArgumentException if the model name is invalid
     */
    ImageModel(String model, Resolution... resolutions) {

        // Initialize attributes
        models = new HashSet<>();
        this.resolutions = new HashSet<>();
        style = new HashSet<>();
        minAmount = 1;
        maxAmount = model.equals("dall-e-2") ? 1 : 10;

        // Set models
        models.add("dall-e-2");
        models.add("dall-e-3");

        // Set Attributes
        if (models.contains(model)) this.model = model;
        else throw new IllegalArgumentException("Invalid model");

        // Set Resolutions
        Collections.addAll(this.resolutions, resolutions);

        if (model.equals("dall-e-2")) {
            minCharacters = 1;
            maxCharacters = 1000;
        } else {
            minCharacters = 1;
            maxCharacters = 4000;
        }

        // Set Style
        if (model.equals("dall-e-2")) return;
        style.add(Style.VIVID);
        style.add(Style.NATURAL);
    }

    /**
     * Retrieves the set of valid model names.
     *
     * @return a {@link HashSet} containing valid model names
     */
    public HashSet<String> getModels() {
        return models;
    }

    /**
     * Retrieves the resolutions supported by the model.
     *
     * @return a {@link HashSet} of {@link Resolution} supported by the model
     */
    public HashSet<Resolution> getResolutions() {
        return resolutions;
    }

    /**
     * Retrieves the name of the model.
     *
     * @return the model name as a {@link String}
     */
    public String getModel() {
        return model;
    }

    /**
     * Retrieves the set of supported styles.
     *
     * @return a {@link HashSet} of {@link Style} supported by the model
     */
    public HashSet<Style> getStyle() {
        return style;
    }

    /**
     * Calculates the price of generating an image based on resolution and quality.
     *
     * @param resolution the resolution of the image
     * @param quality    the quality of the image
     * @return the price as a {@link BigDecimal}
     * @throws IllegalArgumentException if the resolution or quality is invalid
     */
    public BigDecimal getPrice(Resolution resolution, Quality quality) {
        return resolution.getPrice(this, quality);
    }

    /**
     * Validates the model name.
     *
     * @param model the model name to validate
     * @return {@code true} if the model is valid, {@code false} otherwise
     */
    public boolean checkModel(String model) {
        if (model == null || model.isBlank()) return false;
        return models.contains(model);
    }

    /**
     * Validates the prompt based on character limits.
     *
     * @param prompt the prompt to validate
     * @return {@code true} if the prompt length is within the allowed range, {@code false} otherwise
     */
    public boolean checkPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) return false;
        return prompt.length() >= minCharacters && prompt.length() <= maxCharacters;
    }

    /**
     * Validates the number of images requested.
     *
     * @param amount the number of images requested
     * @return {@code true} if the amount is within the valid range, {@code false} otherwise
     */
    public boolean checkAmount(Integer amount) {
        if (amount == null) return false;
        return amount >= minAmount && amount <= maxAmount;
    }

    /**
     * Validates the quality string.
     *
     * @param quality the quality string to validate
     * @return {@code true} if the quality is valid, {@code false} otherwise
     */
    public boolean checkQuality(String quality) {
        if (quality == null || quality.isBlank()) return false;
        if (model.equals("dall-e-2") && quality.equals("standard")) return true;
        else if (model.equals("dall-e-3")) return quality.equals("standard") || quality.equals("hd");
        else return false;
    }

    /**
     * Validates the resolution string.
     *
     * @param resolution the resolution string to validate
     * @return {@code true} if the resolution is valid, {@code false} otherwise
     */
    public boolean checkResolution(String resolution) {
        if (resolution == null || resolution.isBlank()) return false;
        return resolutions.stream().anyMatch(res -> res.checkResolution(resolution));
    }

    /**
     * Validates the style string.
     *
     * @param style the style string to validate
     * @return {@code true} if the style is valid, {@code false} otherwise
     */
    public boolean checkStyle(String style) {
        if (style == null || style.isBlank()) return false;
        if (model.equals("dall-e-2")) return false;
        else return style.equals("vivid") || style.equals("natural");
    }

    /**
     * Represents the supported resolutions for image generation, including their respective pricing.
     */
    public enum Resolution {

        // Resolutions
        RES_256x256("256x256", 0.016),
        RES_512x512("512x512", 0.018),
        RES_1024x1024("1024x1024", 0.04, 0.08),
        RES_1024x1792("1024x1792", 0.08, 0.12),
        RES_1792x1024("1792x1024", 0.12, 0.12);

        // Attributes
        private final HashSet<String> resolutions;
        private final HashSet<Quality> qualities;
        private final String resolution;
        private final BigDecimal standardPrice;
        private final BigDecimal hdPrice;

        /**
         * Constructs a resolution with only standard quality pricing.
         *
         * @param resolution     the resolution string (e.g., "256x256")
         * @param standardPrice  the price for standard quality
         * @throws IllegalArgumentException if the resolution is invalid
         */
        Resolution(String resolution, double standardPrice) {

            // Initialize attributes
            resolutions = new HashSet<>();
            qualities = new HashSet<>();

            // Set resolutions
            resolutions.add("256x256");
            resolutions.add("512x512");
            resolutions.add("1024x1024");
            resolutions.add("1024x1792");
            resolutions.add("1792x1024");

            // Set qualities
            qualities.add(Quality.STANDARD);

            // Set Attributes
            if (resolutions.contains(resolution)) this.resolution = resolution;
            else throw new IllegalArgumentException("Invalid resolution");

            // Set Prices
            this.standardPrice = new BigDecimal(standardPrice);
            hdPrice = null;
        }

        /**
         * Constructs a resolution with both standard and HD quality pricing.
         *
         * @param resolution     the resolution string (e.g., "1024x1024")
         * @param standardPrice  the price for standard quality
         * @param hdPrice        the price for HD quality
         * @throws IllegalArgumentException if the resolution is invalid
         */
        Resolution(String resolution, double standardPrice, double hdPrice) {

            // Initialize attributes
            resolutions = new HashSet<>();
            qualities = new HashSet<>();

            // Set resolutions
            resolutions.add("256x256");
            resolutions.add("512x512");
            resolutions.add("1024x1024");
            resolutions.add("1024x1792");
            resolutions.add("1792x1024");

            // Set qualities
            qualities.add(Quality.STANDARD);
            qualities.add(Quality.HD);

            // Set Attributes
            if (resolutions.contains(resolution)) this.resolution = resolution;
            else throw new IllegalArgumentException("Invalid resolution");

            // Set Prices
            this.standardPrice = new BigDecimal(standardPrice);
            this.hdPrice = new BigDecimal(hdPrice);
        }

        /**
         * Retrieves the resolution string.
         *
         * @return the resolution as a {@link String}
         */
        public String getResolution() {
            return resolution;
        }

        /**
         * Retrieves all valid resolutions.
         *
         * @return a {@link HashSet} of resolution strings
         */
        public HashSet<String> getResolutions() {
            return resolutions;
        }

        /**
         * Retrieves the supported qualities for this resolution.
         *
         * @return a {@link HashSet} of {@link Quality}
         */
        public HashSet<Quality> getQualities() {
            return qualities;
        }

        /**
         * Validates a resolution string.
         *
         * @param resolution the resolution string to validate
         * @return {@code true} if the resolution is valid, {@code false} otherwise
         */
        public boolean checkResolution(String resolution) {
            if (resolution == null || resolution.isBlank()) return false;
            return resolutions.contains(resolution);
        }

        /**
         * Retrieves the price for a specific quality and model.
         *
         * @param model   the {@link ImageModel} associated with the resolution
         * @param quality the {@link Quality} of the image
         * @return the price as a {@link BigDecimal}
         * @throws IllegalArgumentException if the quality or model is invalid
         */
        public BigDecimal getPrice(ImageModel model, Quality quality) {
            if (model == ImageModel.DALL_E_2) {
                if (quality == Quality.STANDARD) return standardPrice;
                else throw new IllegalArgumentException("Invalid quality");
            } else throw new IllegalArgumentException("Invalid model");
        }
    }

    /**
     * Represents the quality levels available for image generation.
     */
    public enum Quality {

        /**
         * Standard quality for image generation.
         */
        STANDARD("standard"),

        /**
         * High-definition (HD) quality for image generation.
         */
        HD("hd");

        // Attributes
        private final HashSet<String> qualities;
        private final String quality;

        /**
         * Constructs a {@code Quality} with the specified quality string.
         *
         * @param quality the quality string (e.g., "standard", "hd")
         * @throws IllegalArgumentException if the quality is invalid
         */
        Quality(String quality) {

            // Initialize attributes
            qualities = new HashSet<>();

            // Set qualities
            qualities.add("standard");
            qualities.add("hd");

            // Set Attributes
            if (qualities.contains(quality)) this.quality = quality;
            else throw new IllegalArgumentException("Invalid quality");
        }

        /**
         * Retrieves all valid quality levels.
         *
         * @return a {@link HashSet} of quality strings
         */
        public HashSet<String> getQualities() {
            return qualities;
        }

        /**
         * Retrieves the quality string.
         *
         * @return the quality as a {@link String}
         */
        public String getQuality() {
            return quality;
        }

        /**
         * Validates a quality string.
         *
         * @param quality the quality string to validate
         * @return {@code true} if the quality is valid, {@code false} otherwise
         */
        public boolean checkQuality(String quality) {
            if (quality == null || quality.isBlank()) return false;
            return qualities.contains(quality);
        }
    }

    /**
     * Represents the artistic styles available for image generation.
     */
    public enum Style {

        /**
         * Vivid artistic style.
         */
        VIVID("vivid"),

        /**
         * Natural artistic style.
         */
        NATURAL("natural");

        // Attributes
        private final HashSet<String> styles;
        private final String style;

        /**
         * Constructs a {@code Style} with the specified style string.
         *
         * @param style the style string (e.g., "vivid", "natural")
         * @throws IllegalArgumentException if the style is invalid
         */
        Style(String style) {

            // Initialize attributes
            styles = new HashSet<>();

            // Set styles
            styles.add("vivid");
            styles.add("natural");

            // Set Attributes
            if (styles.contains(style)) this.style = style;
            else throw new IllegalArgumentException("Invalid style");
        }

        /**
         * Retrieves all valid styles.
         *
         * @return a {@link HashSet} of style strings
         */
        public HashSet<String> getStyles() {
            return styles;
        }

        /**
         * Retrieves the style string.
         *
         * @return the style as a {@link String}
         */
        public String getStyle() {
            return style;
        }

        /**
         * Validates a style string.
         *
         * @param style the style string to validate
         * @return {@code true} if the style is valid, {@code false} otherwise
         */
        public boolean checkStyle(String style) {
            if (style == null || style.isBlank()) return false;
            return styles.contains(style);
        }
    }
}