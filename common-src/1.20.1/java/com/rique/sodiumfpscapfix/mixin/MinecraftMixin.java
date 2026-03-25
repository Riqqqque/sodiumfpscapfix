package com.rique.sodiumfpscapfix.mixin;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @ModifyConstant(method = "runTick", constant = @Constant(intValue = 260))
    private int sodiumfpscapfix$raiseFpsCutoff(int original) {
        return FpsCapConstants.UNLIMITED_CUTOFF;
    }
}
