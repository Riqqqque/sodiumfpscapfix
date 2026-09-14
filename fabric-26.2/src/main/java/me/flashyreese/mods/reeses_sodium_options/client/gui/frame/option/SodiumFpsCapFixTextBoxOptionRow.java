package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option;

import com.rique.sodiumfpscapfix.FpsCapConstants;
import com.rique.sodiumfpscapfix.FpsCapSupport;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.state.OptionStateStore;
import me.flashyreese.mods.reeses_sodium_options.client.gui.theme.GuiTheme;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.gui.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.lang.ref.WeakReference;

public final class SodiumFpsCapFixTextBoxOptionRow extends AbstractOptionRow {
    private static final int BOX_PADDING = 3;
    private static WeakReference<SodiumFpsCapFixTextBoxOptionRow> activeEditor = new WeakReference<>(null);

    private final IntegerOption option;
    private final EditBox editBox;
    private boolean syncingText;

    public SodiumFpsCapFixTextBoxOptionRow(LayoutBounds bounds, GuiTheme theme, OptionStateStore optionStateStore, IntegerOption option) {
        super(bounds, theme, optionStateStore, option);

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
        this.editBox.setResponder(this::onTextChanged);
        this.editBox.setTextColor(Colors.FOREGROUND);
        this.editBox.setTextColorUneditable(Colors.FOREGROUND_DISABLED);

        this.syncFromOption();
    }

    @Override
    public IntegerOption getOption() {
        return this.option;
    }

    @Override
    protected int controlContentWidth() {
        return FpsCapConstants.TEXT_BOX_WIDTH + 12;
    }

    @Override
    protected void renderControl(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!this.editBox.isFocused()) {
            this.syncFromOption();
        }

        this.updateTextBoxBounds();
        this.editBox.setEditable(this.option.isEnabled());
        this.editBox.setVisible(true);

        int boxX = this.getBoxX();
        int boxY = this.getBoxY();
        int boxLimitX = boxX + FpsCapConstants.TEXT_BOX_WIDTH;
        int boxLimitY = boxY + FpsCapConstants.TEXT_BOX_HEIGHT;

        this.drawRect(graphics, boxX, boxY, boxLimitX, boxLimitY, this.option.isEnabled() ? Colors.BACKGROUND_MEDIUM : Colors.BACKGROUND_LIGHT);
        this.drawBorder(graphics, boxX, boxY, boxLimitX, boxLimitY, this.editBox.isFocused() ? this.theme.theme : 0x60FFFFFF);
        this.editBox.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    protected boolean controlMouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.option.isEnabled() || !this.isMouseOverBox(event.x(), event.y())) {
            this.finishEditing();
            return false;
        }

        this.focusEditor();
        this.editBox.mouseClicked(event, doubleClick);
        return true;
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
    protected boolean controlKeyPressed(KeyEvent event) {
        if (!this.editBox.isFocused()
                && this.isRowFocused()
                && this.option.isEnabled()
                && !event.isEscape()
                && !event.isConfirmation()) {
            this.focusEditor();
        }

        if (!this.editBox.isFocused()) {
            return super.controlKeyPressed(event);
        }

        if (event.isEscape() || event.isConfirmation()) {
            this.finishEditing();
            return false;
        }

        return this.editBox.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.editBox.isFocused() && this.isRowFocused() && this.option.isEnabled()) {
            this.focusEditor();
        }

        return this.editBox.isFocused() && this.editBox.charTyped(event);
    }

    @Override
    protected boolean activateControl() {
        if (!this.option.isEnabled()) {
            return false;
        }

        this.focusEditor();
        return true;
    }

    @Override
    public boolean handleBackNavigation() {
        if (!this.editBox.isFocused()) {
            return false;
        }

        this.finishEditing();
        return false;
    }

    @Override
    protected void onControlFocusLost() {
        this.finishEditing();
    }

    @Override
    protected Component narrationValue() {
        return Component.literal(Integer.toString(FpsCapSupport.clamp(this.option.getValidatedValue())));
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

        this.option.modifyValue(FpsCapSupport.parseAndClamp(sanitized));
        this.option.getValidatedValue();
    }

    private void syncFromOption() {
        this.setText(Integer.toString(FpsCapSupport.clamp(this.option.getValidatedValue())));
    }

    private void finishEditing() {
        if (this.editBox.isFocused()) {
            this.syncFromOption();
        }

        this.editBox.setFocused(false);
        this.clearActiveEditor();
    }

    public static boolean finishActiveEditingIfOutside(double mouseX, double mouseY) {
        SodiumFpsCapFixTextBoxOptionRow row = activeEditor.get();
        if (row != null && !row.isMouseOverBox(mouseX, mouseY)) {
            row.finishEditing();
            return true;
        }

        return false;
    }

    public static boolean finishActiveEditing() {
        SodiumFpsCapFixTextBoxOptionRow row = activeEditor.get();
        if (row == null) {
            return false;
        }

        row.finishEditing();
        return true;
    }

    private void focusEditor() {
        this.editBox.setFocused(true);
        activeEditor = new WeakReference<>(this);
    }

    private void clearActiveEditor() {
        if (activeEditor.get() == this) {
            activeEditor.clear();
        }
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
        return this.rightAlignedControlX(FpsCapConstants.TEXT_BOX_WIDTH);
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
