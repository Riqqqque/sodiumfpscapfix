package com.rique.sodiumfpscapfix.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        } catch (Throwable ignored) {
            return hasNeoForgeModClass(className);
        }
    }

    private static boolean hasNeoForgeModClass(String className) {
        try {
            Class<?> loadingModListClass = Class.forName(
                    "net.neoforged.fml.loading.LoadingModList",
                    false,
                    SodiumFpsCapFixMixinPlugin.class.getClassLoader()
            );
            Object loadingModList = loadingModListClass.getMethod("get").invoke(null);

            if (loadingModList == null) {
                return false;
            }

            Object sodiumModFileInfo = loadingModList.getClass()
                    .getMethod("getModFileById", String.class)
                    .invoke(loadingModList, "sodium");

            if (sodiumModFileInfo == null) {
                return false;
            }

            Object sodiumModFile = sodiumModFileInfo.getClass().getMethod("getFile").invoke(sodiumModFileInfo);
            return hasModFileResource(sodiumModFile, className.replace('.', '/') + ".class");
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static boolean hasModFileResource(Object modFile, String resourceName)
            throws ReflectiveOperationException {
        try {
            Object contents = modFile.getClass().getMethod("getContents").invoke(modFile);
            Class<?> contentsClass = Class.forName(
                    "net.neoforged.fml.jarcontents.JarContents",
                    false,
                    SodiumFpsCapFixMixinPlugin.class.getClassLoader()
            );
            return Boolean.TRUE.equals(contentsClass.getMethod("containsFile", String.class)
                    .invoke(contents, resourceName));
        } catch (NoSuchMethodException ignored) {
            String[] resourcePath = resourceName.split("/");
            Path resource = (Path) modFile.getClass()
                    .getMethod("findResource", String[].class)
                    .invoke(modFile, (Object) resourcePath);
            return Files.isRegularFile(resource);
        }
    }
}
