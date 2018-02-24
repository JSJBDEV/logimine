package com.logitow.logimine.client.gui;

import com.logitow.logimine.LogiMine;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Represents the Bluetooth error gui.
 */
public class BluetoothDialogGui extends GuiScreen {
    /**
     * The location of the gui texture.
     */
    final public ResourceLocation guiTexture = new ResourceLocation(LogiMine.modId, "gui/bluetooth-dialog.png");

    /**
     * The height of the container graphic.
     */
    final int containerHeight = 140;
    /**
     * The width of the container graphic.
     */
    final int containerWidth = 248;

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        super.actionPerformed(p_actionPerformed_1_);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return super.doesGuiPauseGame();
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
        super.initGui();
    }
}
