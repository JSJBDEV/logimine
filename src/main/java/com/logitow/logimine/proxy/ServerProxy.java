package com.logitow.logimine.proxy;

import com.logitow.logimine.event.LogitowBridgeServerEventHandler;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by James on 14/12/2017.
 * Modified by itsMatoosh
 */
public class ServerProxy {
    public void registerItemRenderer(Item item, int meta, String id) {
    }
    public void registerLogitowEvents() {
        //Registering the mod side bridge event.
        MinecraftForge.EVENT_BUS.register(LogitowBridgeServerEventHandler.class);
    }
}
