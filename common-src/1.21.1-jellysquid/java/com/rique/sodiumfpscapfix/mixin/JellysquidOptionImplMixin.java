package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapPersistence;
import com.rique.sodiumfpscapfix.JellysquidFpsCapSupport;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me.jellysquid.mods.sodium.client.gui.options.OptionImpl")
public abstract class JellysquidOptionImplMixin {
    @Inject(method = "applyChanges", at = @At("TAIL"))
    private void sodiumfpscapfix$persistAppliedFpsCap(CallbackInfo ci) {
        Option<?> option = (Option<?>) (Object) this;

        if (JellysquidFpsCapSupport.isFrameRateLimitOption(option) && option.getValue() instanceof Integer fpsCap) {
            FpsCapPersistence.save(fpsCap);
        }
    }
}
