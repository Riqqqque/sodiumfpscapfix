package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import com.rique.sodiumfpscapfix.gui.FpsCapTextBoxControl;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.config.structure.IntegerOption")
public abstract class IntegerOptionMixin {
    @Inject(method = "validateValue", at = @At("HEAD"), cancellable = true)
    private void sodiumfpscapfix$allowAnyFpsCap(Integer value, CallbackInfoReturnable<Integer> cir) {
        if (this.sodiumfpscapfix$isFrameRateLimitOption()) {
            cir.setReturnValue(value == null ? FpsCapConstants.MIN_FPS_CAP : FpsCapSupport.clamp(value));
        }
    }

    @Inject(method = "createControl", at = @At("HEAD"), cancellable = true)
    private void sodiumfpscapfix$replaceSlider(CallbackInfoReturnable<Control> cir) {
        if (this.sodiumfpscapfix$isFrameRateLimitOption()) {
            cir.setReturnValue(new FpsCapTextBoxControl((IntegerOption) (Object) this));
        }
    }

    private boolean sodiumfpscapfix$isFrameRateLimitOption() {
        return FpsCapSupport.isSodiumFrameRateLimitId(((OptionAccessor) (Object) this).sodiumfpscapfix$getId())
                || FpsCapSupport.isFrameRateLimitName(((Option) (Object) this).getName());
    }
}
