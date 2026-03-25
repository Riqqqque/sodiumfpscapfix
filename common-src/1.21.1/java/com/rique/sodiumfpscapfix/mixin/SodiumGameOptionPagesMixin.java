package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapSupport;
import com.rique.sodiumfpscapfix.gui.FpsCapTextBoxControl;
import net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SodiumGameOptionPages.class)
public abstract class SodiumGameOptionPagesMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "general", at = @At("RETURN"))
    private static void sodiumfpscapfix$replaceSlider(CallbackInfoReturnable<OptionPage> cir) {
        for (Option<?> option : cir.getReturnValue().getOptions()) {
            if (!FpsCapSupport.isFrameRateLimitOption(option)) {
                continue;
            }

            ((OptionImplAccessor) option).sodiumfpscapfix$setControl(new FpsCapTextBoxControl((Option<Integer>) option));
            return;
        }
    }
}
