package com.rique.sodiumfpscapfix.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlElement;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public final class FpsCapTextBoxControl implements Control<Integer> {
    private final Option<Integer> option;

    public FpsCapTextBoxControl(Option<Integer> option) {
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
        return FpsCapConstants.TEXT_BOX_WIDTH + 12;
    }

    private static final class TextBoxControlElement extends ControlElement<Integer> {
        private static final int BOX_PADDING = 3;

        private final EditBox editBox;
        private boolean syncingText;

        private TextBoxControlElement(Option<Integer> option, Dim2i dim) {
            super(option, dim);

            this.editBox = new EditBox(
                    Minecraft.getInstance().font,
                    0,
                    0,
                    FpsCapConstants.TEXT_BOX_WIDTH - (BOX_PADDING * 2),
                    FpsCapConstants.TEXT_BOX_HEIGHT - 2,
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
        public int getContentWidth() {
            return FpsCapConstants.TEXT_BOX_WIDTH + 12;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            if (!this.editBox.isFocused()) {
                this.syncFromOption();
            }

            this.updateTextBoxBounds();
            this.editBox.setEditable(this.option.isAvailable());

            super.render(graphics, mouseX, mouseY, delta);

            int boxX = this.getBoxX();
            int boxY = this.getBoxY();
            int boxLimitX = boxX + FpsCapConstants.TEXT_BOX_WIDTH;
            int boxLimitY = boxY + FpsCapConstants.TEXT_BOX_HEIGHT;

            this.drawRect(graphics, boxX, boxY, boxLimitX, boxLimitY, this.option.isAvailable() ? 0x70000000 : 0x40000000);
            this.drawBorder(graphics, boxX, boxY, boxLimitX, boxLimitY, this.editBox.isFocused() ? 0xFFFFFFFF : 0x60FFFFFF);
            this.editBox.render(graphics, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.option.isAvailable()) {
                return false;
            }

            boolean clickedRow = this.dim.containsCursor(mouseX, mouseY);
            boolean clickedBox = this.isMouseOverBox(mouseX, mouseY);

            this.focused = clickedRow;
            this.editBox.setFocused(clickedBox);

            if (clickedBox) {
                return this.editBox.mouseClicked(mouseX, mouseY, button);
            }

            return clickedRow;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!this.editBox.isFocused()) {
                return false;
            }

            if (keyCode == InputConstants.KEY_ESCAPE) {
                this.syncFromOption();
                this.editBox.setFocused(false);
                this.focused = false;
                return true;
            }

            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
                this.syncFromOption();
                this.editBox.setFocused(false);
                this.focused = false;
                return true;
            }

            return this.editBox.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char codePoint, int modifiers) {
            return this.editBox.isFocused() && this.editBox.charTyped(codePoint, modifiers);
        }

        @Override
        public void setFocused(boolean focused) {
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

            this.option.setValue(FpsCapSupport.parseAndClamp(text));
        }

        private void syncFromOption() {
            this.setText(Integer.toString(FpsCapSupport.clamp(this.option.getValue())));
        }

        private void setText(String value) {
            this.syncingText = true;
            this.editBox.setValue(value);
            this.syncingText = false;
        }

        private void updateTextBoxBounds() {
            this.editBox.setX(this.getBoxX() + BOX_PADDING);
            this.editBox.setY(this.getBoxY() + 1);
            this.editBox.setWidth(FpsCapConstants.TEXT_BOX_WIDTH - (BOX_PADDING * 2));
        }

        private int getBoxX() {
            return this.dim.getLimitX() - FpsCapConstants.TEXT_BOX_WIDTH - 6;
        }

        private int getBoxY() {
            return this.dim.getCenterY() - (FpsCapConstants.TEXT_BOX_HEIGHT / 2);
        }

        private boolean isMouseOverBox(double mouseX, double mouseY) {
            int boxX = this.getBoxX();
            int boxY = this.getBoxY();

            return mouseX >= boxX && mouseX < boxX + FpsCapConstants.TEXT_BOX_WIDTH
                    && mouseY >= boxY && mouseY < boxY + FpsCapConstants.TEXT_BOX_HEIGHT;
        }
    }
}
