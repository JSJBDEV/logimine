package com.logitow.logimine.client.gui;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.EventHandler;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.DeviceDiscoveredEvent;
import com.logitow.bridge.event.device.DeviceLostEvent;
import com.logitow.logimine.Blocks.BlockKey;
import com.logitow.logimine.LogiMine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the device manager gui window.
 */
public class DeviceManagerGui extends GuiScreen {

    /**
     * The location of the gui texture.
     */
    final public ResourceLocation guiTexture = new ResourceLocation(LogiMine.modId, "gui/device-manager.png");

    /**
     * The height of the container graphic.
     */
    final int containerHeight = 256;
    /**
     * The width of the container graphic.
     */
    final int containerWidth = 162;

    /**
     * The vertical separation of device list buttons.
     */
    final int deviceButtonSeparation = 40;
    /**
     * The list start y coordinate.
     */
    private int getDeviceStartPosition () {
        return height/2 - 50;
    }

    /**
     * The currently discovered devices and their button ids.
     */
    private Map<Device, Integer> discoveredDevices = new HashMap<>();

    /**
     * The accept button.
     */
    GuiButton connectButton;
    final int CONNECT_BUTTON_ID = 0;
    /**
     * The cancel button.
     */
    GuiButton cancelButton;
    final int CANCEL_BUTTON_ID = 1;
    /**
     * The ble scan button.
     */
    GuiButton scanButton;
    final int SCAN_BUTTON_ID = 2;

    /**
     * Event handler for updating the discovered devices list.
     */
    private EventHandler deviceDiscoveredHandler = new EventHandler() {
        @Override
        public void onEventCalled(Event event) {
            DeviceDiscoveredEvent discoveredEvent = (DeviceDiscoveredEvent)event;

            //The new y pos of the element.
            int newYPos = getDeviceStartPosition()-deviceButtonSeparation;

            //Getting the highest y position of the elements.
            for (GuiButton button :
                    buttonList) {
                if(button.id > 100 && button.y > newYPos) {
                    newYPos = button.y;
                }
            }

            //Adding the new device.
            int buttonId = 100 + buttonList.size()-3;
            buttonList.add(new GuiButton(buttonId, width/2 - 70, newYPos + deviceButtonSeparation, 140, 20, discoveredEvent.device.toString()));
            discoveredDevices.put(discoveredEvent.device, buttonId);
        }
    };
    /**
     * Event handler for updateing the discovered devices list.
     */
    private EventHandler deviceLostHandler = new EventHandler() {
        @Override
        public void onEventCalled(Event event) {
            DeviceLostEvent lostEvent = (DeviceLostEvent)event;

            //Updating the list.
            int removeButton = -1;
            for (Device discoveredDevice:
                 discoveredDevices.keySet()) {
                if(discoveredDevice.info.uuid == lostEvent.device.info.uuid) {
                    removeButton = discoveredDevices.get(discoveredDevice);
                    discoveredDevices.remove(discoveredDevice);
                }
            }

            //Updating the buttons.
            GuiButton buttonToRemove = null;
            int lastY = getDeviceStartPosition()-deviceButtonSeparation;
            for (GuiButton button :
                    buttonList) {
                if(button.id == removeButton) {
                    buttonToRemove = button;
                }
                else if(button.id > 100) {
                    button.y = lastY + deviceButtonSeparation;
                    lastY = button.y;
                }
            }


            //Removing the button.
            buttonList.remove(buttonToRemove);
        }
    };

    /**
     * Whether the window is open.
     */
    private boolean opened = false;

    /**
     * The currently selected key block to attach devices to.
     * NULL if no block selected.
     */
    public static BlockKey selectedKeyBlock = null;

    /**
     * The currently selected device.
     */
    private int deviceChosen = -1;

    /**
     * Draws the screen and all the components in it.
     *
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);

        //Getting the center coords.
        int centerX = (width/2) - containerWidth/2;
        int centerY = (height/2) - containerHeight/2;

        drawTexturedModalRect(centerX,centerY,0,0,containerWidth,containerHeight);
        drawString(fontRenderer, "Logitow", (width/2) - fontRenderer.getStringWidth("Logitow")/2, (height/2) - containerHeight/3 +5, 0x00ff00);

        for (GuiButton button :
                buttonList) {
            if(button.id == deviceChosen) {
                button.enabled = false;
            } else {
                button.enabled = true;
            }
        }

        if(selectedKeyBlock != null) {
            drawString(fontRenderer, "Select Device", (width/2) - fontRenderer.getStringWidth("Select Device")/2, (height/2) - containerHeight/3 +17, 0x8b8b8b);
            if(deviceChosen != -1) {
                connectButton.enabled = true;
            } else {
                connectButton.enabled = false;
            }
        } else {
            drawString(fontRenderer, "Available Devices", (width/2) - fontRenderer.getStringWidth("Available Devices")/2, (height/2) - containerHeight/3 +17, 0x8b8b8b);
            connectButton.enabled = false;
        }

        //Drawing the scanning string.
        if(LogitowDeviceManager.current.isScanning) {
            drawString(fontRenderer, "Scanning...", (width/2) - fontRenderer.getStringWidth("Scanning...")/2, (height/2) + 20, 0x8b8b8b);
            scanButton.enabled = false;
        } else {
            scanButton.enabled = true;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui() {
        if(!opened) {
            //Registering events.
            EventManager.registerHandler(deviceDiscoveredHandler, DeviceDiscoveredEvent.class);
            EventManager.registerHandler(deviceLostHandler, DeviceLostEvent.class);
        }
        opened = true;

        //Setting up buttons.
        buttonList.clear();
        int buttonSeparation = 50;
        int buttonWidth = 50;

        //Accept bttn
        buttonList.add(connectButton = new GuiButton(CONNECT_BUTTON_ID, width/2 - buttonWidth/2 - buttonSeparation, height/2 + 100, buttonWidth, 20, "Connect"));

        //Scan bttn
        buttonList.add(scanButton = new GuiButton(SCAN_BUTTON_ID, width/2 - buttonWidth/2, height/2 + 100, buttonWidth, 20, "Scan"));

        //Cancel bttn
        buttonList.add(cancelButton = new GuiButton(CANCEL_BUTTON_ID, width/2 - buttonWidth/2 + buttonSeparation, height/2 + 100, buttonWidth, 20, "Cancel"));
        super.initGui();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     *
     * @param button
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case CONNECT_BUTTON_ID:
                for (Device device :
                        discoveredDevices.keySet()) {
                    if(discoveredDevices.get(device) == deviceChosen) {
                        //Found the appropriate device.
                        if(LogiMine.assignedDevices.containsKey(device.info.uuid)) {
                            LogiMine.assignedDevices.replace(device.info.uuid, selectedKeyBlock);
                        } else {
                            LogiMine.assignedDevices.put(device.info.uuid, selectedKeyBlock);
                        }
                        //System.out.println("Assigned device " + device + " to the key block!");
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Assigned device " + device + " to the key block!"));
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Connecting to device " + device));

                        device.connect();
                    }
                }
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case CANCEL_BUTTON_ID:
                //Closing the hud.
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case SCAN_BUTTON_ID:
                //Starting device scan with the logitow-bridge library.
                if(LogitowDeviceManager.current.isScanning) {
                    LogitowDeviceManager.current.stopDeviceDiscovery();
                }
                LogitowDeviceManager.current.startDeviceDiscovery();
                break;
        }

        //Device button has been selected.
        if(button.id >= 100) {
            if(selectedKeyBlock != null) {
                System.out.println("Selected device " + deviceChosen);
                deviceChosen = button.id;
            }
        }

        super.actionPerformed(button);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed() {
        if(LogitowDeviceManager.current.isScanning) {
            //Stop device scan.
            LogitowDeviceManager.current.stopDeviceDiscovery();
        }

        //Unregister event listeners.
        selectedKeyBlock = null;
        EventManager.unregisterHandler(deviceDiscoveredHandler, DeviceDiscoveredEvent.class);
        EventManager.unregisterHandler(deviceLostHandler, DeviceLostEvent.class);

        opened = false;
        super.onGuiClosed();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
