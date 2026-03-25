package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapPersistence;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.UnlockedFpsCapValueSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow
    public abstract OptionInstance<Integer> framerateLimit();

    @ModifyArgs(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/OptionInstance;<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/util/function/Consumer;)V"
            )
    )
    private void sodiumfpscapfix$unlockFpsLimit(Args args) {
        if ("options.framerateLimit".equals(args.get(0))) {
            args.set(3, new UnlockedFpsCapValueSet(args.get(3)));
            args.set(4, FpsCapSupport.codec());
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void sodiumfpscapfix$loadSavedFpsLimit(CallbackInfo ci) {
        FpsCapPersistence.load().ifPresent(this.framerateLimit()::set);
    }
}
