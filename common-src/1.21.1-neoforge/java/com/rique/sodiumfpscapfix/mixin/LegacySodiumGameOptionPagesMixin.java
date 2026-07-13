package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.LegacyFpsCapSupport;
import com.rique.sodiumfpscapfix.gui.LegacyFpsCapTextBoxControl;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages")
public abstract class LegacySodiumGameOptionPagesMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "general", at = @At("RETURN"))
    private static void sodiumfpscapfix$replaceSlider(CallbackInfoReturnable<OptionPage> cir) {
        for (Option<?> option : cir.getReturnValue().getOptions()) {
            if (!LegacyFpsCapSupport.isFrameRateLimitOption(option)) {
                continue;
            }

            ((LegacyOptionImplAccessor) option).sodiumfpscapfix$setControl(new LegacyFpsCapTextBoxControl((Option<Integer>) option));
            return;
        }
    }
}
