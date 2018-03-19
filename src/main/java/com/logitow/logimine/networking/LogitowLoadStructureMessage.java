package com.logitow.logimine.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Asks the server to load a specific structure to a keyblock.
 */
public class LogitowLoadStructureMessage implements IMessage {

    /**
     * The keyblock to load the structure to.
     */
    public BlockPos keyBlock;

    /**
     * The name of the structure to load.
     */
    public String structureName;

    public LogitowLoadStructureMessage() {}
    public LogitowLoadStructureMessage(BlockPos blockPos, String structureToLoad) {
        this.keyBlock = blockPos;
        this.structureName = structureToLoad;
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
