package com.logitow.logimine.proxy;

import com.logitow.bridge.communication.BluetoothState;
import com.logitow.bridge.communication.Device;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.client.gui.*;
import com.logitow.logimine.event.LogitowBridgeClientEventHandler;
import com.logitow.logimine.networking.LogitowEventForwarder;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by James on 14/12/2017.
 */
public class ClientProxy extends ServerProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(LogiMine.modId + ":" + id, "inventory"));
    }

    /**
     * Calls the events locally but also forwards them to the server.
     */
    @Override
    public void registerLogitowEvents() {
        //Registering the mod side bridge event.
        MinecraftForge.EVENT_BUS.register(LogitowBridgeClientEventHandler.class);
        MinecraftForge.EVENT_BUS.register(LogitowEventForwarder.class);
    }

    public void setSelectedKeyBlock(BlockPos blockKey) {
        if(HubGui.instance != null) {
            HubGui.setSelectedKeyBlock(blockKey);
        }
    }

    public void showClientGui(int type) {
        switch (type) {
            case 0:
                Minecraft.getMinecraft().displayGuiScreen(new HubGui());
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(new DeviceManagerGui());
                break;
            case 2:
                Minecraft.getMinecraft().displayGuiScreen(new LoadStructureGui());
                break;
        }
    }

    public void showSaveStructureGui(TileEntityBlockKey blockKey) {
        Minecraft.getMinecraft().displayGuiScreen(new SaveStructureGui(blockKey));
    }
    public void showBluetoothDialogGui(BluetoothState state) {
        Minecraft.getMinecraft().displayGuiScreen(new BluetoothDialogGui(state));
    }
    public void closeManagersWhenDestroyed(BlockPos destroyed) {
        if(HubGui.instance != null && HubGui.getSelectedKeyBlock() != null && HubGui.getSelectedKeyBlock().getPos().equals(destroyed)) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    public void showConnectNotification(Device device) {
        NotificationToast.showConnect(device);
    }
    public void showDisconnectNotification(Device device) {
        NotificationToast.showDisconnect(device);
    }
    public void notifySavedStructuresPageLoaded(StructuresPage page) {
        //Passing the received page to the gui.
        if(LoadStructureGui.instance != null) {
            LoadStructureGui.instance.onPageLoaded(page);
        }
    }
}
