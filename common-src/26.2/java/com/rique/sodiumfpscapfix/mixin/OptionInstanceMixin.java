package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.gui.VanillaFpsCapTextBox;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OptionInstance.class)
public abstract class OptionInstanceMixin<T> {
    @Inject(
            method = "createButton(Lnet/minecraft/client/Options;IIILnet/minecraft/client/OptionInstance$ValueUpdateListener;)Lnet/minecraft/client/gui/components/AbstractWidget;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sodiumfpscapfix$replaceVanillaFpsSlider(Options options, int x, int y, int width, OptionInstance.ValueUpdateListener<? super T> updateListener, CallbackInfoReturnable<AbstractWidget> cir) {
        OptionInstance<?> option = (OptionInstance<?>) (Object) this;

        if (option != options.framerateLimit()) {
            return;
        }

        @SuppressWarnings("unchecked")
        OptionInstance<Integer> fpsOption = (OptionInstance<Integer>) option;
        @SuppressWarnings("unchecked")
        OptionInstance.ValueUpdateListener<? super Integer> fpsUpdateListener = (OptionInstance.ValueUpdateListener<? super Integer>) updateListener;

        cir.setReturnValue(VanillaFpsCapTextBox.create(fpsOption, x, y, width, fpsUpdateListener));
    }
}
