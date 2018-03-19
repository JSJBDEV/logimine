package com.logitow.logimine.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.nio.charset.Charset;

/**
 * Signals the server to save the given structures to a file.
 */
public class LogitowSaveStructureMessage implements IMessage {

    /**
     * The keyblock which structures to save.
     */
    public BlockPos keyBlock;

    /**
     * The name under which to save the structures.
     */
    public String name;

    public LogitowSaveStructureMessage(BlockPos keyBlock, String name){
        this.keyBlock = keyBlock;
        if (name == null || name == "") {
            name = "Untitled";
        }
        this.name = name;
    }

    public LogitowSaveStructureMessage() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.keyBlock = BlockPos.fromLong(buf.readLong());
        int nameLenght = buf.readInt();
        byte[] nameData = new byte[nameLenght];
        buf.readBytes(nameData);

        this.name = new String(nameData, Charset.forName("UTF-8"));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        //blockpos
        buf.writeLong(this.keyBlock.toLong());

        //name
        byte[] nameData = this.name.getBytes(Charset.forName("UTF-8"));
        buf.writeInt(nameData.length);
        buf.writeBytes(nameData);
    }
}
