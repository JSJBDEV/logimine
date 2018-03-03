package com.logitow.logimine.networking;

import com.logitow.bridge.communication.Device;
import com.logitow.logimine.blocks.ModBlocks;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.Charset;

/**
 * Message for assigning LOGITOW devices to key blocks.
 */
public class LogitowDeviceAssignMessage implements IMessage {
    /**
     * Position of the key block.
     */
    public BlockPos position;
    /**
     * UUID of the assigned device.
     */
    public String deviceUUID;

    public LogitowDeviceAssignMessage(BlockPos keyBlock, Device device) {
        this.position = keyBlock;
        this.deviceUUID = device.info.uuid;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        position = BlockPos.fromLong(buf.readLong());
        int uuidLength = buf.readInt();
        deviceUUID = new String(buf.readBytes(uuidLength).array(), Charset.forName("UTF-8"));
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(position.toLong());
        byte[] uuid = deviceUUID.getBytes(Charset.forName("UTF-8"));
        buf.writeInt(uuid.length);
        buf.writeBytes(uuid);
    }

    /**
     * Handler for the assign device message.
     */
    public class LogitowDeviceAssignMessageHandler implements IMessageHandler<LogitowDeviceAssignMessage, IMessage> {

        /**
         * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
         * is needed.
         *
         * @param message The message
         * @param ctx
         * @return an optional return message
         */
        @Override
        public IMessage onMessage(LogitowDeviceAssignMessage message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            //Execute the action on the main server thread by adding it as a scheduled task
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                //Checking if the block is a key block.
                if(serverPlayer.getServerWorld().getBlockState(message.position) == ModBlocks.key_lblock.getDefaultState()) {
                    //Assigning the key block.

                } else {
                    serverPlayer.sendMessage(new TextComponentString("Can't assign device a LOGITOW device to this block!"));
                }
            });

            return null;
        }
    }
}
