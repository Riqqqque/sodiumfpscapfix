package net.minecraft.client;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class UnlockedFpsCapValueSet implements OptionInstance.ValueSet<Integer> {
    private final OptionInstance.ValueSet<Integer> delegate;

    @SuppressWarnings("unchecked")
    public UnlockedFpsCapValueSet(Object delegate) {
        this.delegate = (OptionInstance.ValueSet<Integer>) delegate;
    }

    @Override
    public Function<OptionInstance<Integer>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<Integer> tooltip, Options options, int x, int y, int width, Consumer<Integer> changeCallback) {
        return this.delegate.createButton(tooltip, options, x, y, width, changeCallback);
    }

    @Override
    public Optional<Integer> validateValue(Integer value) {
        return Optional.of(FpsCapSupport.clamp(value == null ? FpsCapConstants.MIN_FPS_CAP : value));
    }

    @Override
    public com.mojang.serialization.Codec<Integer> codec() {
        return FpsCapSupport.codec();
    }
}
