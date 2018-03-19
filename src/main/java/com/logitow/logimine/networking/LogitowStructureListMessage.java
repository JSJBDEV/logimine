package com.logitow.logimine.networking;

import com.logitow.logimine.client.gui.LoadStructureGui;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * A response message of the server with the requested list of saved structures.
 */
public class LogitowStructureListMessage implements IMessage {

    /**
     * The requested saved strcures page.
     */
    public LoadStructureGui.StructuresPage requestedPage;

    public LogitowStructureListMessage() {}
    public LogitowStructureListMessage(LoadStructureGui.StructuresPage structuresPage) {
        this.requestedPage = structuresPage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        //TODO
    }

    @Override
    public void toBytes(ByteBuf buf) {
        //TODO
    }
}
