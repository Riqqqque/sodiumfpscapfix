package com.rique.sodiumfpscapfix.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlElement;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public final class JellysquidFpsCapTextBoxControl implements Control<Integer> {
    private static final int VALUE_WIDTH = 90;
    private static final int VALUE_HEIGHT = 12;
    private static final int VALUE_RIGHT_MARGIN = 6;
    private static final int BOX_PADDING = 3;

    private final Option<Integer> option;

    public JellysquidFpsCapTextBoxControl(Option<Integer> option) {
        this.option = option;
    }

    @Override
    public Option<Integer> getOption() {
        return this.option;
    }

    @Override
    public ControlElement<Integer> createElement(Dim2i dim) {
        return new TextBoxControlElement(this.option, dim);
    }

    @Override
    public int getMaxWidth() {
        return VALUE_WIDTH + VALUE_RIGHT_MARGIN;
    }

    private static final class TextBoxControlElement extends ControlElement<Integer> {
        private final EditBox editBox;
        private boolean syncingText;

        private TextBoxControlElement(Option<Integer> option, Dim2i dim) {
            super(option, dim);

            this.editBox = new EditBox(
                    Minecraft.getInstance().font,
                    0,
                    0,
                    VALUE_WIDTH - (BOX_PADDING * 2),
                    VALUE_HEIGHT - 2,
                    Component.empty()
            );
            this.editBox.setBordered(false);
            this.editBox.setMaxLength(Integer.toString(FpsCapConstants.MAX_FPS_CAP).length());
            this.editBox.setFilter(text -> text.isEmpty() || text.chars().allMatch(FpsCapSupport::isAsciiDigit));
            this.editBox.setResponder(this::onTextChanged);
            this.editBox.setTextColor(0xFFFFFFFF);
            this.editBox.setTextColorUneditable(0xFFA0A0A0);

            this.syncFromOption();
        }

        @Override
        public void method_25394(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            super.method_25394(graphics, mouseX, mouseY, delta);

            if (!this.editBox.isFocused()) {
                this.syncFromOption();
            }

            this.updateTextBoxBounds();
            this.editBox.setEditable(this.option.isAvailable());

            int boxX = this.getBoxX();
            int boxY = this.getBoxY();
            int boxLimitX = boxX + VALUE_WIDTH;
            int boxLimitY = boxY + VALUE_HEIGHT;
            int backgroundColor = this.option.isAvailable()
                    ? (this.editBox.isFocused() ? 0x50000000 : 0x33000000)
                    : 0x22000000;
            int borderColor = this.editBox.isFocused()
                    ? 0xFFFFFFFF
                    : (this.hovered ? 0x70FFFFFF : 0x30FFFFFF);

            this.drawRect(graphics, boxX, boxY, boxLimitX, boxLimitY, backgroundColor);
            this.drawBorder(graphics, boxX, boxY, boxLimitX, boxLimitY, borderColor);
            this.editBox.render(graphics, mouseX, mouseY, delta);
        }

        public boolean method_25402(double mouseX, double mouseY, int button) {
            if (!this.option.isAvailable()) {
                return false;
            }

            boolean clickedRow = this.dim.containsCursor(mouseX, mouseY);
            boolean clickedBox = this.isMouseOverBox(mouseX, mouseY);

            this.focused = clickedRow;
            this.editBox.setFocused(clickedBox);

            if (clickedBox) {
                this.editBox.mouseClicked(mouseX, mouseY, button);
                return true;
            }

            return clickedRow;
        }

        public boolean method_25404(int keyCode, int scanCode, int modifiers) {
            if (!this.editBox.isFocused()) {
                return false;
            }

            if (keyCode == InputConstants.KEY_ESCAPE
                    || keyCode == InputConstants.KEY_RETURN
                    || keyCode == InputConstants.KEY_NUMPADENTER) {
                this.syncFromOption();
                this.editBox.setFocused(false);
                this.focused = false;
                return true;
            }

            return this.editBox.keyPressed(keyCode, scanCode, modifiers);
        }

        public boolean method_25400(char codePoint, int modifiers) {
            return this.editBox.isFocused() && this.editBox.charTyped(codePoint, modifiers);
        }

        @Override
        public void method_25365(boolean focused) {
            this.focused = focused;
            this.editBox.setFocused(focused);

            if (!focused) {
                this.syncFromOption();
            }
        }

        private void onTextChanged(String text) {
            if (this.syncingText || text.isEmpty()) {
                return;
            }

            int value = FpsCapSupport.parseAndClamp(text);

            this.option.setValue(value);
        }

        private void syncFromOption() {
            this.setText(Integer.toString(FpsCapSupport.clamp(this.option.getValue())));
        }

        private void setText(String value) {
            this.syncingText = true;

            try {
                this.editBox.setValue(value);
            } finally {
                this.syncingText = false;
            }
        }

        private void updateTextBoxBounds() {
            this.editBox.setX(this.getBoxX() + BOX_PADDING);
            this.editBox.setY(this.getTextY());
            this.editBox.setWidth(this.getInnerBoxWidth());
        }

        private int getInnerBoxWidth() {
            return VALUE_WIDTH - (BOX_PADDING * 2);
        }

        private int getTextY() {
            return this.getBoxY() + Math.max(0, (VALUE_HEIGHT - Minecraft.getInstance().font.lineHeight + 1) / 2);
        }

        private int getBoxX() {
            return this.dim.getLimitX() - VALUE_WIDTH - VALUE_RIGHT_MARGIN;
        }

        private int getBoxY() {
            return this.dim.getCenterY() - (VALUE_HEIGHT / 2);
        }

        private boolean isMouseOverBox(double mouseX, double mouseY) {
            int boxX = this.getBoxX();
            int boxY = this.getBoxY();

            return mouseX >= boxX && mouseX < boxX + VALUE_WIDTH
                    && mouseY >= boxY && mouseY < boxY + VALUE_HEIGHT;
        }
    }
}
