package com.rique.sodiumfpscapfix;

import me.jellysquid.mods.sodium.client.gui.options.Option;

public final class JellysquidFpsCapSupport {
    private JellysquidFpsCapSupport() {
    }

    public static boolean isFrameRateLimitOption(Option<?> option) {
        return option != null && FpsCapSupport.isFrameRateLimitName(option.getName());
    }
}
