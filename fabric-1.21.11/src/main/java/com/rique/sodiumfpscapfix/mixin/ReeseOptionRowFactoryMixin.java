package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapSupport;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.SodiumFpsCapFixTextBoxOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRowFactory")
public abstract class ReeseOptionRowFactoryMixin {
    @Shadow(remap = false)
    @Final
    private GuiTheme theme;

    @Shadow(remap = false)
    @Final
    private OptionStateStore optionStateStore;

    @Inject(method = "create", at = @At("HEAD"), cancellable = true, remap = false)
    private void sodiumfpscapfix$replaceFpsSlider(Option option, LayoutBounds bounds, CallbackInfoReturnable<Object> cir) {
        if (option instanceof IntegerOption integerOption
                && (FpsCapSupport.isSodiumFrameRateLimitId(((OptionAccessor) option).sodiumfpscapfix$getId())
                || FpsCapSupport.isFrameRateLimitName(option.getName()))) {
            cir.setReturnValue(new SodiumFpsCapFixTextBoxOptionRow(bounds, this.theme, this.optionStateStore, integerOption));
        }
    }
}
