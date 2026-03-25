package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapPersistence;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionImpl.class)
public abstract class OptionImplMixin {
    @Inject(method = "applyChanges", at = @At("TAIL"))
    private void sodiumfpscapfix$persistAppliedFpsCap(CallbackInfo ci) {
        Option<?> option = (Option<?>) (Object) this;

        if (FpsCapSupport.isFrameRateLimitOption(option) && option.getValue() instanceof Integer fpsCap) {
            FpsCapPersistence.save(fpsCap);
        }
    }
}
