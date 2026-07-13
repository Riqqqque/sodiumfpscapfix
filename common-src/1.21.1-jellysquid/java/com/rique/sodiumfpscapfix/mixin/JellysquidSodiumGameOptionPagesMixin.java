package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.JellysquidFpsCapSupport;
import com.rique.sodiumfpscapfix.gui.JellysquidFpsCapTextBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages")
public abstract class JellysquidSodiumGameOptionPagesMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "general", at = @At("RETURN"))
    private static void sodiumfpscapfix$replaceSlider(CallbackInfoReturnable<OptionPage> cir) {
        for (Option<?> option : cir.getReturnValue().getOptions()) {
            if (!JellysquidFpsCapSupport.isFrameRateLimitOption(option)) {
                continue;
            }

            ((JellysquidOptionImplAccessor) option).sodiumfpscapfix$setControl(new JellysquidFpsCapTextBoxControl((Option<Integer>) option));
            return;
        }
    }
}
