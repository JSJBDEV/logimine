package com.logitow.logimine.client.gui;

import com.logitow.bridge.communication.BluetoothState;
import com.logitow.logimine.LogiMine;
import net.minecraft.client.Minecraft;
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

    /**
     * The current state of the bluetooth adapter.
     */
    private BluetoothState currentBluetoothState;

    /**
     * ID of the close dialog button.
     */
    final int CLOSE_DIALOG_BUTTON_ID = 0;

    /**
     * Creates the dialog based on the bluetooth state.
     * @param bluetoothState
     */
    public BluetoothDialogGui(BluetoothState bluetoothState) {
        this.currentBluetoothState = bluetoothState;
    }

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        Minecraft.getMinecraft().displayGuiScreen(null);

        super.actionPerformed(p_actionPerformed_1_);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        drawDefaultBackground();
        Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);

        //Getting the center coords.
        int centerX = (width/2) - containerWidth/2;
        int centerY = (height/2) - containerHeight/2;

        drawTexturedModalRect(centerX,centerY,4,58,containerWidth,containerHeight);

        //Text depending on bluetooth state.
        String infoText = "";

        if(currentBluetoothState == BluetoothState.PoweredOn) {
            //Bluetooth enabled, closing the dialog.
            Minecraft.getMinecraft().displayGuiScreen(null);
        } else if(currentBluetoothState == BluetoothState.PoweredOff) {
            drawString(fontRenderer, "Enable Bluetooth!", (width/2) - fontRenderer.getStringWidth("Enable Bluetooth!")/2 + 42, (height/2) - containerHeight/2+30, 0xff0055);
            infoText = "Make sure that Bluetooth\nfunctionality is enabled\non your system.";
        } else {
            drawString(fontRenderer, "Couldn't access Bluetooth!", (width/2) - fontRenderer.getStringWidth("Couldn't access Bluetooth!")/2+42, (height/2) - containerHeight/2+30, 0xff0055);
            infoText = "Make sure that your \nsystem supports \nBluetooth Low Energy \nand no other application \nis using it.";
        }

        //Drawing each line.
        int lineOffset = 0;
        for (String line :
                infoText.split("\n")) {
            drawString(fontRenderer, line, (width/2) - fontRenderer.getStringWidth(line)/2 + 42, (height/2) - containerHeight/2+47 + lineOffset, 0xffffff);
            lineOffset += 11;
        }

        super.drawScreen(p_drawScreen_1_,p_drawScreen_2_,p_drawScreen_3_);
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(CLOSE_DIALOG_BUTTON_ID, width/2 - 40 + 42, height/2 + 30, 80, 20, "Dismiss"));
        super.initGui();
    }

}
