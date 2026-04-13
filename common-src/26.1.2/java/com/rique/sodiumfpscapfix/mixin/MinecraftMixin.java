package com.rique.sodiumfpscapfix.mixin;

import com.mojang.blaze3d.platform.FramerateLimitTracker;
import net.minecraft.client.FramerateLimiter;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    private static final int VANILLA_UNCAPPED_MARKER = 260;

    @Shadow
    private FramerateLimitTracker framerateLimitTracker;

    @Inject(method = "runTick(Z)V", at = @At("TAIL"))
    private void sodiumfpscapfix$limitHighFpsCaps(boolean renderLevel, CallbackInfo ci) {
        int fpsCap = this.framerateLimitTracker.getFramerateLimit();

        if (fpsCap >= VANILLA_UNCAPPED_MARKER) {
            FramerateLimiter.limitDisplayFPS(fpsCap);
        }
    }
}
