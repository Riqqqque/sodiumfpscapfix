package com.rique.sodiumfpscapfix.mixin;

import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionImpl.class)
public interface OptionImplAccessor {
    @Mutable
    @Accessor("control")
    void sodiumfpscapfix$setControl(Control<?> control);
}
