package com.rique.sodiumfpscapfix.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.config.structure.Option")
public interface OptionAccessor {
    @Accessor("id")
    ResourceLocation sodiumfpscapfix$getId();
}
