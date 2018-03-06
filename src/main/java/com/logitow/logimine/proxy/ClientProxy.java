package com.logitow.logimine.proxy;

import com.logitow.logimine.event.LogitowBridgeClientEventHandler;
import com.logitow.logimine.networking.LogitowEventForwarder;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import com.logitow.logimine.LogiMine;
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
}
