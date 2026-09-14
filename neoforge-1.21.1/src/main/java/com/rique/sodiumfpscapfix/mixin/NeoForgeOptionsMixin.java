package com.rique.sodiumfpscapfix.mixin;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Options.class)
public abstract class NeoForgeOptionsMixin {
    @ModifyConstant(
            method = "lambda$new$6(I)Ljava/lang/Integer;",
            constant = @Constant(intValue = 10),
            require = 1
    )
    private static int sodiumfpscapfix$keepFpsLimitSliderValueExact(int original) {
        return 1;
    }

    @ModifyConstant(
            method = "lambda$new$7(Ljava/lang/Integer;)I",
            constant = @Constant(intValue = 10),
            require = 1
    )
    private static int sodiumfpscapfix$keepFpsLimitOptionValueExact(int original) {
        return 1;
    }
}
