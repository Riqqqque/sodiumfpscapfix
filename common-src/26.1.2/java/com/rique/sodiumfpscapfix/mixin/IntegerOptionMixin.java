package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import com.rique.sodiumfpscapfix.gui.FpsCapTextBoxControl;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegerOption.class)
public abstract class IntegerOptionMixin {
    @Inject(method = "validateValue", at = @At("HEAD"), cancellable = true)
    private void sodiumfpscapfix$allowAnyFpsCap(Integer value, CallbackInfoReturnable<Integer> cir) {
        if (FpsCapSupport.isFrameRateLimitId(((OptionAccessor) this).sodiumfpscapfix$getId())) {
            if (value == null) {
                cir.setReturnValue(FpsCapConstants.MIN_FPS_CAP);
            } else if (value < FpsCapConstants.MIN_FPS_CAP) {
                cir.setReturnValue(FpsCapConstants.MIN_FPS_CAP);
            } else if (value > FpsCapConstants.MAX_FPS_CAP) {
                cir.setReturnValue(FpsCapConstants.MAX_FPS_CAP);
            } else {
                cir.setReturnValue(value);
            }
        }
    }

    @Inject(method = "createControl", at = @At("HEAD"), cancellable = true)
    private void sodiumfpscapfix$replaceSlider(CallbackInfoReturnable<Control> cir) {
        if (FpsCapSupport.isFrameRateLimitId(((OptionAccessor) this).sodiumfpscapfix$getId())) {
            cir.setReturnValue(new FpsCapTextBoxControl((IntegerOption) (Object) this));
        }
    }
}
