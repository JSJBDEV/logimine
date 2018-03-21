package com.logitow.logimine.networking;

import com.logitow.bridge.event.Event;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.io.*;

/**
 * Packet with structures update info.
 * Sent from client to server.
 */
public class LogitowEventMessage implements IMessage {

    /**
     * Event transmitted.
     */
    public Event event;

    public LogitowEventMessage(){}

    /**
     * Creates the update packet given the block operation event.
     * @param event
     */
    public LogitowEventMessage(Event event) {
        this.event = event;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        try {
            this.event = (Event)deserialize(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        byte[] data = new byte[0];
        try {
            data = serialize(this.event);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
