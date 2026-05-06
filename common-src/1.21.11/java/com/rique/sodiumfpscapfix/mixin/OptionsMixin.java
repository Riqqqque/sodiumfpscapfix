package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapPersistence;
import com.rique.sodiumfpscapfix.FpsCapConstants;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow
    public abstract OptionInstance<Integer> framerateLimit();

    @ModifyConstant(
            method = "<init>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=options.framerateLimit")
            ),
            constant = @Constant(intValue = 26, ordinal = 0),
            require = 1
    )
    private int sodiumfpscapfix$unlockFpsLimitRange(int original) {
        return FpsCapConstants.MAX_FPS_CAP / 10;
    }

    @ModifyConstant(
            method = "<init>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=options.framerateLimit")
            ),
            constant = @Constant(intValue = 260, ordinal = 0),
            require = 1
    )
    private int sodiumfpscapfix$unlockFpsLimitCodecRange(int original) {
        return FpsCapConstants.MAX_FPS_CAP;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void sodiumfpscapfix$loadSavedFpsLimit(CallbackInfo ci) {
        FpsCapPersistence.load().ifPresent(this.framerateLimit()::set);
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void sodiumfpscapfix$syncSavedFpsLimit(CallbackInfo ci) {
        FpsCapPersistence.save(this.framerateLimit().get());
    }
}
