package com.rique.sodiumfpscapfix.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class ReeseMixinPlugin implements IMixinConfigPlugin {
    private static final String OPTION_ROW_FACTORY = "me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRowFactory";
    private static final String VIDEO_OPTIONS_SCREEN = "me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen";

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
        if (!hasClass(OPTION_ROW_FACTORY) || !hasClass(VIDEO_OPTIONS_SCREEN)) {
            return List.of();
        }

        return List.of("ReeseOptionRowFactoryMixin", "ReeseScreenInputMixin");
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
