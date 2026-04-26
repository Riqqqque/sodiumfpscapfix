package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.AccurateFramerateLimiter;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @ModifyConstant(method = "renderFrame", constant = @Constant(intValue = 260))
    private int sodiumfpscapfix$raiseFpsCutoff(int original) {
        return FpsCapConstants.UNLIMITED_CUTOFF;
    }

    @Redirect(
            method = "renderFrame",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/FramerateLimiter;limitDisplayFPS(I)V"
            )
    )
    private void sodiumfpscapfix$limitDisplayFps(int framerateLimit) {
        AccurateFramerateLimiter.limitDisplayFPS(framerateLimit);
    }
}
