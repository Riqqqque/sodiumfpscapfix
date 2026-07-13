package com.rique.sodiumfpscapfix.gui;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.caffeinemc.mods.sodium.client.gui.Colors;
import net.caffeinemc.mods.sodium.client.gui.options.control.AbstractOptionList;
import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlElement;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class FpsCapTextBoxControl implements Control {
    private final IntegerOption option;

    public FpsCapTextBoxControl(IntegerOption option) {
        this.option = option;
    }

    @Override
    public Option getOption() {
        return this.option;
    }

    @Override
    public ControlElement createElement(Screen screen, AbstractOptionList list, Dim2i dim, ColorTheme theme) {
        return new TextBoxControlElement(list, this.option, dim, theme);
    }

    @Override
    public int getMaxWidth() {
        return FpsCapConstants.TEXT_BOX_WIDTH + 12;
    }

    private static final class TextBoxControlElement extends ControlElement {
        private static final int BOX_PADDING = 3;

        private final IntegerOption option;
        private final EditBox editBox;
        private boolean syncingText;

        private TextBoxControlElement(AbstractOptionList list, IntegerOption option, Dim2i dim, ColorTheme theme) {
            super(list, dim, theme);

            this.option = option;
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
            this.editBox.setTextColor(Colors.FOREGROUND);
            this.editBox.setTextColorUneditable(Colors.FOREGROUND_DISABLED);

            this.syncFromOption();
        }

        @Override
        public Option getOption() {
            return this.option;
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
            this.editBox.setEditable(this.option.isEnabled());
            this.editBox.setVisible(true);

            super.render(graphics, mouseX, mouseY, delta);

            int boxX = this.getBoxX();
            int boxY = this.getBoxY();
            int boxLimitX = boxX + FpsCapConstants.TEXT_BOX_WIDTH;
            int boxLimitY = boxY + FpsCapConstants.TEXT_BOX_HEIGHT;

            this.drawRect(graphics, boxX, boxY, boxLimitX, boxLimitY, this.option.isEnabled() ? Colors.BACKGROUND_MEDIUM : Colors.BACKGROUND_LIGHT);
            this.drawBorder(graphics, boxX, boxY, boxLimitX, boxLimitY, this.editBox.isFocused() ? Colors.BUTTON_BORDER : 0x60FFFFFF);
            this.editBox.render(graphics, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!this.option.isEnabled()) {
                return false;
            }

            boolean clickedRow = this.isMouseOver(event.x(), event.y());
            boolean clickedBox = this.isMouseOverBox(event.x(), event.y());

            this.focused = clickedRow;
            this.editBox.setFocused(clickedBox);

            if (clickedBox) {
                this.editBox.mouseClicked(event, doubleClick);
                return true;
            }

            return clickedRow;
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            return this.editBox.isFocused() && this.editBox.mouseReleased(event);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
            return this.editBox.isFocused() && this.editBox.mouseDragged(event, deltaX, deltaY);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (!this.editBox.isFocused()) {
                return false;
            }

            if (event.isEscape() || event.isConfirmation()) {
                this.syncFromOption();
                this.editBox.setFocused(false);
                this.focused = false;
                return true;
            }

            return this.editBox.keyPressed(event);
        }

        @Override
        public boolean charTyped(CharacterEvent event) {
            return this.editBox.isFocused() && this.editBox.charTyped(event);
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

            int value = FpsCapSupport.parseAndClamp(text);
            this.option.modifyValue(value);
            this.option.getValidatedValue();
        }

        private void syncFromOption() {
            this.setText(Integer.toString(FpsCapSupport.clamp(this.option.getValidatedValue())));
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
            return FpsCapConstants.TEXT_BOX_WIDTH - (BOX_PADDING * 2);
        }

        private int getTextY() {
            return this.getBoxY() + Math.max(0, (FpsCapConstants.TEXT_BOX_HEIGHT - Minecraft.getInstance().font.lineHeight + 1) / 2);
        }

        private int getBoxX() {
            return this.getLimitX() - FpsCapConstants.TEXT_BOX_WIDTH - 6;
        }

        private int getBoxY() {
            return this.getCenterY() - (FpsCapConstants.TEXT_BOX_HEIGHT / 2);
        }

        private boolean isMouseOverBox(double mouseX, double mouseY) {
            int boxX = this.getBoxX();
            int boxY = this.getBoxY();

            return mouseX >= boxX && mouseX < boxX + FpsCapConstants.TEXT_BOX_WIDTH
                    && mouseY >= boxY && mouseY < boxY + FpsCapConstants.TEXT_BOX_HEIGHT;
        }
    }
}
