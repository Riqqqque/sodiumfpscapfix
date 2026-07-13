package com.rique.sodiumfpscapfix.mixin;

import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.options.OptionImpl")
public interface LegacyOptionImplAccessor {
    @Mutable
    @Accessor("control")
    void sodiumfpscapfix$setControl(Control<?> control);
}
