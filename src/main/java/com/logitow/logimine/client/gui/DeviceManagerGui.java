package com.logitow.logimine.client.gui;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.device.DeviceDiscoveredEvent;
import com.logitow.bridge.event.device.DeviceLostEvent;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.event.LogitowBridgeEvent;
import com.logitow.logimine.networking.LogitowDeviceAssignMessage;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the device manager gui window.
 *
 * Created by itsMatoosh
 */
@SideOnly(Side.CLIENT)
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
     * Id of the assigned device button.
     */
    int ASSIGNED_DEVICE_BUTTON_ID = -1;

    /**
     * Whether the window is open.
     */
    private boolean opened = false;

    /**
     * The currently selected key block to attach devices to.
     * NULL if no block selected.
     */
    public TileEntityBlockKey selectedKeyBlock = null;

    /**
     * The currently selected device.
     */
    private int deviceChosen = -1;

    /**
     * The currently open gui.
     */
    public static DeviceManagerGui instance;

    //Translated elements.
    public static final ITextComponent TEXT_DEVICE_MANAGER_TITLE = new TextComponentTranslation("logitow.devicemanager.title");
    public static final ITextComponent TEXT_DEVICE_MANAGER_SELECT_DEVICE = new TextComponentTranslation("logitow.devicemanager.selectdevice");
    public static final ITextComponent TEXT_DEVICE_MANAGER_AVAILEBLE_DEVICES = new TextComponentTranslation("logitow.devicemanager.availabledevices");
    public static final ITextComponent TEXT_DEVICE_MANAGER_SCANNING = new TextComponentTranslation("logitow.devicemanager.scanning");
    public static final ITextComponent TEXT_DEVICE_MANAGER_CONNECT_BUTTON = new TextComponentTranslation("logitow.devicemanager.connectbutton");
    public static final ITextComponent TEXT_DEVICE_MANAGER_DISCONNECT_BUTTON = new TextComponentTranslation("logitow.devicemanager.disconnectbutton");
    public static final ITextComponent TEXT_DEVICE_MANAGER_SCAN_BUTTON = new TextComponentTranslation("logitow.devicemanager.scanbutton");
    public static final ITextComponent TEXT_DEVICE_MANAGER_CANCEL_BUTTON = new TextComponentTranslation("logitow.devicemanager.cancelbutton");
    public static final ITextComponent TEXT_DEVICE_MANAGER_KEYBLOCK_NOT_INITIALIZED = new TextComponentTranslation("logitow.devicemanager.keyblocknotinitialized");
    public static final ITextComponent TEXT_DEVICE_MANAGER_BLOCK_IS_NOT_KEY = new TextComponentTranslation("logitow.devicemanager.blockisnotkeyblock");
    public static final String TEXT_DEVICE_MANAGER_CONNECTING = "logitow.devicemanager.connecting";
    public static final String TEXT_DEVICE_MANAGER_DISCONNECTING = "logitow.devicemanager.disconnecting";
    public static final String TEXT_DEVICE_MANAGER_ASSIGNED = "logitow.devicemanager.assigneddevice";
    public static final String TEXT_DEVICE_MANAGER_UNASSIGNED = "logitow.devicemanager.unassigneddevice";

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
        String title = TEXT_DEVICE_MANAGER_TITLE.getFormattedText();
        drawString(fontRenderer, title, (width/2) - fontRenderer.getStringWidth(title)/2, (height/2) - containerHeight/3 +5, 0x00ff00);

        for (GuiButton button :
                buttonList) {
            if(button.id == deviceChosen) {
                button.enabled = false;
            } else {
                button.enabled = true;
            }
        }

        if(selectedKeyBlock != null) {
            String selectDevice = TEXT_DEVICE_MANAGER_SELECT_DEVICE.getFormattedText();
            drawString(fontRenderer, selectDevice, (width/2) - fontRenderer.getStringWidth(selectDevice)/2, (height/2) - containerHeight/3 +17, 0x8b8b8b);
            if(deviceChosen != -1) {
                connectButton.enabled = true;
            } else {
                connectButton.enabled = false;
            }
        } else {
            String availableDevices = TEXT_DEVICE_MANAGER_AVAILEBLE_DEVICES.getFormattedText();
            drawString(fontRenderer, availableDevices, (width/2) - fontRenderer.getStringWidth(availableDevices)/2, (height/2) - containerHeight/3 +17, 0x8b8b8b);
            connectButton.enabled = false;
        }

        //Drawing the scanning string.
        if(LogitowDeviceManager.current.isScanning) {
            String scanning = TEXT_DEVICE_MANAGER_SCANNING.getFormattedText();
            drawString(fontRenderer, scanning, (width/2) - fontRenderer.getStringWidth(scanning)/2, (height/2) + 20, 0x8b8b8b);
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
            MinecraftForge.EVENT_BUS.register(this);
            instance = this;
        }
        opened = true;

        //Setting up buttons.
        buttonList.clear();
        int buttonSeparation = 50;
        int buttonWidth = 50;

        //Accept bttn
        buttonList.add(connectButton = new GuiButton(CONNECT_BUTTON_ID, width/2 - buttonWidth/2 - buttonSeparation, height/2 + 100, buttonWidth, 20, TEXT_DEVICE_MANAGER_CONNECT_BUTTON.getFormattedText()));

        //Scan bttn
        buttonList.add(scanButton = new GuiButton(SCAN_BUTTON_ID, width/2 - buttonWidth/2, height/2 + 100, buttonWidth, 20, TEXT_DEVICE_MANAGER_SCAN_BUTTON.getFormattedText()));

        //Cancel bttn
        buttonList.add(cancelButton = new GuiButton(CANCEL_BUTTON_ID, width/2 - buttonWidth/2 + buttonSeparation, height/2 + 100, buttonWidth, 20, TEXT_DEVICE_MANAGER_CANCEL_BUTTON.getFormattedText()));
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
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(TEXT_DEVICE_MANAGER_CONNECTING, device.info.friendlyName));

                        if(instance != null && instance.selectedKeyBlock != null && instance.selectedKeyBlock.getAssignedDevice() != null) {
                            instance.selectedKeyBlock.getAssignedDevice().disconnect();
                        }

                        //Found the appropriate device. Temporarily assigning.
                        selectedKeyBlock.assignDevice(Minecraft.getMinecraft().player, device);

                        //Avoiding slowdowns.
                        new Thread(() -> device.connect()).start();

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
            if(button.id == ASSIGNED_DEVICE_BUTTON_ID) {
                //Disconnecting the device.
                Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(TEXT_DEVICE_MANAGER_DISCONNECTING, selectedKeyBlock.getAssignedDevice().info.friendlyName));
                selectedKeyBlock.getAssignedDevice().disconnect();
                button.enabled = false;
                System.out.println("Disconnecting device: " + selectedKeyBlock.getAssignedDevice() + ", unassigning it from block " + selectedKeyBlock);

                //Unassigning
                selectedKeyBlock.assignDevice(null, null);
                LogiMine.networkWrapper.sendToServer(new LogitowDeviceAssignMessage(selectedKeyBlock.getPos(), null));

                //Updating the buttons.
                GuiButton buttonToRemove = null;
                int lastY = getDeviceStartPosition()-deviceButtonSeparation;
                for (GuiButton registered :
                        buttonList) {
                    if(registered.id == ASSIGNED_DEVICE_BUTTON_ID) {
                        buttonToRemove = registered;
                    }
                    else if(registered.id > 100) {
                        registered.y = lastY + deviceButtonSeparation;
                        lastY = registered.y;
                    }
                }

                //Removing the button.
                buttonList.remove(buttonToRemove);

                //Closing the dialog.
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
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
        MinecraftForge.EVENT_BUS.unregister(this);
        instance = null;

        opened = false;
        super.onGuiClosed();
    }

    /**
     * Called when the selected key block is resolved.
     * @param pos
     */
    public static void onKeyBlockAssigned(BlockPos pos) {
        //Assigning the key block.
        TileEntity te = Minecraft.getMinecraft().world.getTileEntity(pos);
        if(te == null || !(te instanceof TileEntityBlockKey)) return;
        instance.selectedKeyBlock = (TileEntityBlockKey)te;

        if(instance.selectedKeyBlock.getAssignedDevice() != null) {
            //The new y pos of the assigned device element.
            int newYPos = instance.getDeviceStartPosition() - instance.deviceButtonSeparation;

            //Getting the highest y position of the elements.
            for (GuiButton button :
                    instance.buttonList) {
                if (button.id > 100 && button.y > newYPos) {
                    newYPos = button.y;
                }
            }

            //Adding the new device.
            int buttonId = 100 + instance.buttonList.size() - 3;
            instance.ASSIGNED_DEVICE_BUTTON_ID = buttonId;
            instance.buttonList.add(new GuiButton(buttonId, instance.width / 2 - 70, newYPos + instance.deviceButtonSeparation, 140, 20, "[" + TEXT_DEVICE_MANAGER_DISCONNECT_BUTTON.getFormattedText().toUpperCase() + "] " + instance.selectedKeyBlock.getAssignedDevice()));
            System.out.println("Set the key block reference for device: " + instance.selectedKeyBlock.getAssignedDevice());
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Logitow event handler.
     * @param event
     */
    @SubscribeEvent
    public void logitowEventReceived(LogitowBridgeEvent event) {
        if(event.deviceEvent instanceof DeviceDiscoveredEvent) {
            DeviceDiscoveredEvent discoveredEvent = (DeviceDiscoveredEvent)event.deviceEvent;

            if(discoveredEvent.device == selectedKeyBlock.getAssignedDevice()) return;

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
        } else if(event.deviceEvent instanceof DeviceLostEvent) {
            DeviceLostEvent lostEvent = (DeviceLostEvent)event.deviceEvent;

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
    }
}
