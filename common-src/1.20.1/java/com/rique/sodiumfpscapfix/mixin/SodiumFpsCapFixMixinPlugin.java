package com.rique.sodiumfpscapfix.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class SodiumFpsCapFixMixinPlugin implements IMixinConfigPlugin {
    private static final String OPTION_IMPL = "me.jellysquid.mods.sodium.client.gui.options.OptionImpl";
    private static final String GAME_OPTION_PAGES = "me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages";

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
        if (!hasClass(OPTION_IMPL) || !hasClass(GAME_OPTION_PAGES)) {
            return List.of();
        }

        return List.of("OptionImplAccessor", "OptionImplMixin", "SodiumGameOptionPagesMixin");
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
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
