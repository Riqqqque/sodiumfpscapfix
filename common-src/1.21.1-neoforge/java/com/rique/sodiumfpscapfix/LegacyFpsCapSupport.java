package com.rique.sodiumfpscapfix;

import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.minecraft.network.chat.Component;

public final class LegacyFpsCapSupport {
    private static final String FPS_LIMIT_KEY = "options.framerateLimit";
    private static final Component FPS_LIMIT_NAME = Component.translatable(FPS_LIMIT_KEY);

    private LegacyFpsCapSupport() {
    }

    public static boolean isFrameRateLimitOption(Option<?> option) {
        return option != null && isFrameRateLimitName(option.getName());
    }

    private static boolean isFrameRateLimitName(Component name) {
        return name != null && (FPS_LIMIT_NAME.equals(name) || name.getString().equals(FPS_LIMIT_NAME.getString()));
    }
}
