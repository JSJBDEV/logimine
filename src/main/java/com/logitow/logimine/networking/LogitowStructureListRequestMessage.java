package com.logitow.logimine.networking;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Used by the clients to request fragments of the saved structure list.
 */
public class LogitowStructureListRequestMessage implements IMessage {

    /**
     * The page requested.
     */
    public int requestedPage;

    public LogitowStructureListRequestMessage() {}
    public LogitowStructureListRequestMessage(int requestedPage) {
        this.requestedPage = requestedPage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.requestedPage = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.requestedPage);
    }
}
