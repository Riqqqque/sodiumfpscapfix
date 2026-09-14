package com.rique.sodiumfpscapfix.gui;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public final class VanillaFpsCapTextBox {
    private static final Component LABEL = Component.translatable("options.framerateLimit");
    private static final int HEIGHT = 20;
    private static final int MIN_TEXT_BOX_WIDTH = 56;

    private VanillaFpsCapTextBox() {
    }

    public static AbstractWidget create(OptionInstance<Integer> option, int x, int y, int width, Consumer<Integer> updateListener) {
        Font font = Minecraft.getInstance().font;
        FpsCapWidget widget = new FpsCapWidget(font, x, y, width, HEIGHT);
        FpsCapEditBox editBox = widget.editBox;
        TextResponder responder = new TextResponder(editBox, option, updateListener);

        editBox.setMaxLength(Integer.toString(FpsCapConstants.MAX_FPS_CAP).length());
        editBox.setResponder(responder::onTextChanged);
        editBox.setFocusLostListener(responder::syncFromOption);
        responder.syncFromOption();

        return widget;
    }

    private static final class TextResponder {
        private final FpsCapEditBox editBox;
        private final OptionInstance<Integer> option;
        private final Consumer<Integer> updateListener;
        private boolean syncingText;

        private TextResponder(FpsCapEditBox editBox, OptionInstance<Integer> option, Consumer<Integer> updateListener) {
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
            this.updateListener.accept(value);
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

    private static final class FpsCapWidget extends AbstractWidget {
        private final Font font;
        private final FpsCapEditBox editBox;

        private FpsCapWidget(Font font, int x, int y, int width, int height) {
            super(x, y, width, height, LABEL);
            this.font = font;
            this.editBox = new FpsCapEditBox(font, 0, 0, FpsCapConstants.TEXT_BOX_WIDTH, height, LABEL);
        }

        @Override
        public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            this.updateEditBoxBounds();

            int availableLabelWidth = Math.max(0, this.getWidth() - this.editBox.getWidth() - 12);
            int labelY = this.getY() + Math.max(0, (this.getHeight() - this.font.lineHeight) / 2);
            String text = this.font.plainSubstrByWidth(this.getMessage().getString(), availableLabelWidth);

            graphics.text(this.font, text, this.getX() + 4, labelY, this.active ? 0xFFFFFFFF : 0xFFA0A0A0);
            this.editBox.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            this.updateEditBoxBounds();

            if (!this.active || !this.editBox.isMouseOver(event.x(), event.y())) {
                this.setFocused(false);
                return false;
            }

            this.setFocused(true);
            return this.editBox.mouseClicked(event, doubleClick);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            return this.editBox.isFocused() && this.editBox.mouseReleased(event);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
            return this.editBox.isFocused() && this.editBox.mouseDragged(event, dragX, dragY);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (!this.editBox.isFocused()) {
                return false;
            }

            if (event.isEscape() || event.isConfirmation()) {
                this.setFocused(false);
                return false;
            }

            return this.editBox.keyPressed(event);
        }

        @Override
        public boolean charTyped(CharacterEvent event) {
            return this.editBox.isFocused() && this.editBox.charTyped(event);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            this.editBox.setFocused(focused);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            this.editBox.updateWidgetNarration(output);
        }

        private void updateEditBoxBounds() {
            int labelWidth = this.font.width(this.getMessage());
            int boxWidth = Math.min(
                    FpsCapConstants.TEXT_BOX_WIDTH,
                    Math.max(MIN_TEXT_BOX_WIDTH, this.getWidth() - labelWidth - 12)
            );
            this.editBox.setX(this.getX() + this.getWidth() - boxWidth);
            this.editBox.setY(this.getY());
            this.editBox.setWidth(boxWidth);
            this.editBox.active = this.active;
            this.editBox.setVisible(this.visible);
            this.editBox.setEditable(this.active);
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
