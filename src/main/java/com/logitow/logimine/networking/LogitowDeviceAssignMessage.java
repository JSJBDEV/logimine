package com.logitow.logimine.networking;

import com.logitow.bridge.communication.Device;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

    public LogitowDeviceAssignMessage(){}

    public LogitowDeviceAssignMessage(BlockPos keyBlock, Device device) {
        this.position = keyBlock;
        if(device == null) {
            this.deviceUUID = "NULL";
        } else {
            this.deviceUUID = device.info.uuid;
        }
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
        byte[] data = new byte[uuidLength];
        buf.readBytes(data);
        deviceUUID = new String(data, Charset.forName("UTF-8"));
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
}
