package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapPersistence;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatefulOption.class)
public abstract class StatefulOptionMixin {
    @Inject(method = "applyChanges", at = @At("TAIL"))
    private void sodiumfpscapfix$persistAppliedFpsCap(CallbackInfoReturnable<Boolean> cir) {
        if (!Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }

        if (!FpsCapSupport.isFrameRateLimitId(((OptionAccessor) this).sodiumfpscapfix$getId())) {
            return;
        }

        Object value = ((StatefulOption<?>) (Object) this).getAppliedValue();

        if (value instanceof Integer fpsCap) {
            FpsCapPersistence.save(fpsCap);
        }
    }
}
