package com.rique.sodiumfpscapfix.gui;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public final class VanillaFpsCapTextBox {
    private static final Component LABEL = Component.translatable("options.framerateLimit");
    private static final int HEIGHT = 20;

    private VanillaFpsCapTextBox() {
    }

    public static AbstractWidget create(OptionInstance<Integer> option, int x, int y, int width, OptionInstance.ValueUpdateListener<? super Integer> updateListener) {
        Font font = Minecraft.getInstance().font;
        FpsCapEditBox editBox = new FpsCapEditBox(font, x, y, width, HEIGHT, LABEL);
        TextResponder responder = new TextResponder(editBox, option, updateListener);

        editBox.setMaxLength(Integer.toString(FpsCapConstants.MAX_FPS_CAP).length());
        editBox.setResponder(responder::onTextChanged);
        editBox.setFocusLostListener(responder::syncFromOption);
        responder.syncFromOption();

        return editBox;
    }

    private static final class TextResponder {
        private final FpsCapEditBox editBox;
        private final OptionInstance<Integer> option;
        private final OptionInstance.ValueUpdateListener<? super Integer> updateListener;
        private boolean syncingText;

        private TextResponder(FpsCapEditBox editBox, OptionInstance<Integer> option, OptionInstance.ValueUpdateListener<? super Integer> updateListener) {
            this.editBox = editBox;
            this.option = option;
            this.updateListener = updateListener;
        }

        private void onTextChanged(String text) {
            if (this.syncingText) {
                return;
            }

            String sanitized = sanitizeText(text);

            if (!sanitized.equals(text)) {
                this.setText(sanitized);
                return;
            }

            if (sanitized.isEmpty()) {
                return;
            }

            int value = FpsCapSupport.parseAndClamp(sanitized);

            if (value == this.option.get()) {
                return;
            }

            this.option.set(value);
            this.updateListener.valueChanged(value);
        }

        private void syncFromOption() {
            this.setText(Integer.toString(FpsCapSupport.clamp(this.option.get())));
        }

        private void setText(String value) {
            this.syncingText = true;

            try {
                this.editBox.setValue(value);
            } finally {
                this.syncingText = false;
            }
        }

        private static String sanitizeText(String text) {
            if (text.isEmpty()) {
                return text;
            }

            StringBuilder builder = new StringBuilder(text.length());

            for (int i = 0; i < text.length(); i++) {
                char character = text.charAt(i);

                if (FpsCapSupport.isAsciiDigit(character)) {
                    builder.append(character);
                }
            }

            return builder.toString();
        }
    }

    private static final class FpsCapEditBox extends EditBox {
        private Runnable focusLostListener = () -> {
        };

        private FpsCapEditBox(Font font, int x, int y, int width, int height, Component message) {
            super(font, x, y, width, height, message);
        }

        private void setFocusLostListener(Runnable focusLostListener) {
            this.focusLostListener = focusLostListener;
        }

        @Override
        public void setFocused(boolean focused) {
            boolean wasFocused = this.isFocused();
            super.setFocused(focused);

            if (wasFocused && !focused) {
                this.focusLostListener.run();
            }
        }
    }
}
