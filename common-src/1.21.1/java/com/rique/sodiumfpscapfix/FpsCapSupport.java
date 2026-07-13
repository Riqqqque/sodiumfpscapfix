package com.rique.sodiumfpscapfix;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class FpsCapSupport {
    private static final String FPS_LIMIT_KEY = "options.framerateLimit";
    private static final Component FPS_LIMIT_NAME = Component.translatable(FPS_LIMIT_KEY);
    private static final ResourceLocation SODIUM_FPS_LIMIT_ID = ResourceLocation.fromNamespaceAndPath("sodium", "general.framerate_limit");
    private static final Codec<Integer> CODEC = Codec.INT;

    private FpsCapSupport() {
    }

    public static boolean isFrameRateLimitName(Component name) {
        return name != null && (FPS_LIMIT_NAME.equals(name) || name.getString().equals(FPS_LIMIT_NAME.getString()));
    }

    public static boolean isSodiumFrameRateLimitId(ResourceLocation id) {
        return SODIUM_FPS_LIMIT_ID.equals(id);
    }

    public static int clamp(int value) {
        if (value < FpsCapConstants.MIN_FPS_CAP) {
            return FpsCapConstants.MIN_FPS_CAP;
        }

        return Math.min(value, FpsCapConstants.MAX_FPS_CAP);
    }

    public static int parseAndClamp(String text) {
        long value = 0L;

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);

            if (!isAsciiDigit(character)) {
                throw new NumberFormatException("Unexpected character: " + character);
            }

            int digit = character - '0';

            if (value > ((long) FpsCapConstants.MAX_FPS_CAP - digit) / 10L) {
                return FpsCapConstants.MAX_FPS_CAP;
            }

            value = (value * 10L) + digit;

            if (value >= FpsCapConstants.MAX_FPS_CAP) {
                return FpsCapConstants.MAX_FPS_CAP;
            }
        }

        return clamp((int) value);
    }

    public static boolean isAsciiDigit(int character) {
        return character >= '0' && character <= '9';
    }

    public static Codec<Integer> codec() {
        return CODEC;
    }
}
