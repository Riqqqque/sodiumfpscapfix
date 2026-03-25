package com.rique.sodiumfpscapfix;

import com.mojang.serialization.Codec;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import net.minecraft.network.chat.Component;

public final class FpsCapSupport {
    private static final String FPS_LIMIT_KEY = "options.framerateLimit";
    private static final Codec<Integer> CODEC = Codec.INT;

    private FpsCapSupport() {
    }

    public static boolean isFrameRateLimitOption(Option<?> option) {
        return isFrameRateLimitName(option.getName());
    }

    public static boolean isFrameRateLimitName(Component name) {
        return name.getString().equals(Component.translatable(FPS_LIMIT_KEY).getString());
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

            if (!Character.isDigit(character)) {
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

    public static Codec<Integer> codec() {
        return CODEC;
    }
}
