package com.logitow.logimine.networking;

import com.logitow.logimine.client.gui.StructuresPage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.io.*;

/**
 * A response message of the server with the requested list of saved structures.
 */
public class LogitowStructureListMessage implements IMessage {

    /**
     * The requested saved strcures page.
     */
    public StructuresPage requestedPage;

    public LogitowStructureListMessage() {}
    public LogitowStructureListMessage(StructuresPage structuresPage) {
        this.requestedPage = structuresPage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] serializedData = new byte[buf.readInt()];
        buf.readBytes(serializedData);

        ByteArrayInputStream bis = new ByteArrayInputStream(serializedData);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            this.requestedPage = (StructuresPage)in.readObject();
        } catch (IOException e) {
            //ignore
        } catch (ClassNotFoundException e) {
            //ignore
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this.requestedPage);
            out.flush();
            byte[] serializedBytes = bos.toByteArray();
            buf.writeInt(serializedBytes.length);
            buf.writeBytes(serializedBytes);
        } catch (IOException e) {
            //ignore serialize exceptions
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
}
