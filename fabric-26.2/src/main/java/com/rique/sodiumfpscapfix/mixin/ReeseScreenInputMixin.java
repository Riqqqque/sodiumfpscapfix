package com.rique.sodiumfpscapfix.mixin;

import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.SodiumFpsCapFixTextBoxOptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.FlatButtonWidget;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SodiumVideoOptionsScreen.class)
public abstract class ReeseScreenInputMixin {
    @Invoker("updateControls")
    protected abstract void sodiumfpscapfix$updateControls();

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void sodiumfpscapfix$finishEditorBeforeOutsideClick(
            MouseButtonEvent event,
            boolean doubleClick,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!SodiumFpsCapFixTextBoxOptionRow.finishActiveEditingIfOutside(event.x(), event.y())
                || event.button() != 0) {
            return;
        }

        SodiumVideoOptionsScreen screen = (SodiumVideoOptionsScreen) (Object) this;
        if (this.sodiumfpscapfix$clickToolbarButton(screen.rso$getApplyButton(), event, doubleClick)
                || this.sodiumfpscapfix$clickToolbarButton(screen.rso$getUndoButton(), event, doubleClick)
                || this.sodiumfpscapfix$clickToolbarButton(screen.rso$getCloseButton(), event, doubleClick)) {
            this.sodiumfpscapfix$updateControls();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void sodiumfpscapfix$finishEditorBeforeConfirmation(
            KeyEvent event,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (event.isEscape()) {
            SodiumFpsCapFixTextBoxOptionRow.finishActiveEditing();
            return;
        }

        if (!event.isConfirmation() || !SodiumFpsCapFixTextBoxOptionRow.finishActiveEditing()) {
            return;
        }

        if (ConfigManager.CONFIG.anyOptionChanged()) {
            ConfigManager.CONFIG.applyAllOptions();
        }

        this.sodiumfpscapfix$updateControls();
        cir.setReturnValue(true);
    }

    private boolean sodiumfpscapfix$clickToolbarButton(
            FlatButtonWidget button,
            MouseButtonEvent event,
            boolean doubleClick
    ) {
        return button != null && button.mouseClicked(event, doubleClick);
    }
}
