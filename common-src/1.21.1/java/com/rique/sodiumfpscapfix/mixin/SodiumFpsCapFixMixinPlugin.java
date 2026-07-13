package com.rique.sodiumfpscapfix.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SodiumFpsCapFixMixinPlugin implements IMixinConfigPlugin {
    private static final String MODERN_OPTION = "net.caffeinemc.mods.sodium.client.config.structure.Option";
    private static final String MODERN_INTEGER_OPTION = "net.caffeinemc.mods.sodium.client.config.structure.IntegerOption";
    private static final String MODERN_STATEFUL_OPTION = "net.caffeinemc.mods.sodium.client.config.structure.StatefulOption";
    private static final String LEGACY_OPTION_IMPL = "net.caffeinemc.mods.sodium.client.gui.options.OptionImpl";
    private static final String LEGACY_GAME_OPTION_PAGES = "net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages";
    private static final String JELLYSQUID_OPTION_IMPL = "me.jellysquid.mods.sodium.client.gui.options.OptionImpl";
    private static final String JELLYSQUID_GAME_OPTION_PAGES = "me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        List<String> mixins = new ArrayList<>();

        if (hasAll(MODERN_OPTION, MODERN_INTEGER_OPTION, MODERN_STATEFUL_OPTION)) {
            mixins.add("OptionAccessor");
            mixins.add("IntegerOptionMixin");
            mixins.add("StatefulOptionMixin");
        }

        if (hasAll(LEGACY_OPTION_IMPL, LEGACY_GAME_OPTION_PAGES)) {
            mixins.add("LegacyOptionImplAccessor");
            mixins.add("LegacyOptionImplMixin");
            mixins.add("LegacySodiumGameOptionPagesMixin");
        }

        if (hasAll(JELLYSQUID_OPTION_IMPL, JELLYSQUID_GAME_OPTION_PAGES)) {
            mixins.add("JellysquidOptionImplAccessor");
            mixins.add("JellysquidOptionImplMixin");
            mixins.add("JellysquidSodiumGameOptionPagesMixin");
        }

        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean hasAll(String... classNames) {
        for (String className : classNames) {
            if (!hasClass(className)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasClass(String className) {
        try {
            MixinService.getService().getBytecodeProvider().getClassNode(className, false);
            return true;
        } catch (ClassNotFoundException | IOException ignored) {
            return false;
        }
    }
}
