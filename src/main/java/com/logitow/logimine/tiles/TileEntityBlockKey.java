package com.logitow.logimine.tiles;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.build.Vec3;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.build.block.BlockType;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.blocks.BlockBase;
import com.logitow.logimine.client.gui.SaveStructureGui;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

/**
 * Tile entity of the key block.
 */
public class TileEntityBlockKey extends TileEntity {

    /**
     * The player assigned to this key block.
     */
    private EntityPlayer assignedPlayer;
    /**
     * The device assigned to this key block.
     */
    private Device assignedDevice;
    /**
     * The structures currently assigned to this key block.
     */
    private Structure assignedStructure;

    private static Logger logger = LogManager.getLogger(TileEntityBlockKey.class);

    final ITextComponent TEXT_CANT_ROTATE_NOT_ATTACHED = new TextComponentTranslation("logitow.structure.cantrotatenotattached");
    final String TEXT_ROTATED = "logitow.structure.rotated";

    /**
     * Registering the tile entity with the active key blocks.
     */
    public TileEntityBlockKey() {
        //Remove duplicates.
        TileEntityBlockKey duplicate = null;
        for (TileEntityBlockKey key :
                LogiMine.activeKeyBlocks) {
            if (key.getPos().equals(this.getPos())) {
                duplicate = key;
                break;
            }
        }
        if(duplicate != null) {
            LogiMine.activeKeyBlocks.remove(duplicate);
        }
        //Adding the block to the keyblock list.
        LogiMine.activeKeyBlocks.add(this);
    }

    /**
     * Writes the vars from this object to nbt.
     * @param compound
     * @return
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        logger.info("Saving NBT for key block: {}. Side {}", getPos(), FMLCommonHandler.instance().getEffectiveSide());

        super.writeToNBT(compound);
        NBTTagCompound logitowTag = new NBTTagCompound();
        if(assignedStructure != null) { //Assigned structures
            if(assignedStructure.customName != null && assignedStructure.customName != "") {
                logger.info("Saving {} as structures name", assignedStructure.customName);
                logitowTag.setString("structures", assignedStructure.customName);
            } else {
                logger.info("Saving {} as structures UUID", assignedStructure.uuid);
                logitowTag.setString("structures", assignedStructure.uuid.toString());
            }
        } else {
            logger.info("Removing tag...");
            logitowTag.setString("structures", "NULL");
        }
        if(assignedPlayer != null) { //Assigned player
            logitowTag.setString("player", assignedPlayer.getUniqueID().toString());
        } else {
            logitowTag.setString("player", "NULL");
        }

        compound.setTag("LOGITOW", logitowTag);
        return compound;
    }

    /**
     * Reads vars from nbt to this object.
     * @param compound
     */
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("LOGITOW")) {
            logger.info("Loading NBT for key block: {} Side: {}", getPos(), FMLCommonHandler.instance().getEffectiveSide());

            NBTTagCompound logitowTag = compound.getCompoundTag("LOGITOW");

            //Structure
            logger.info("NBT: Structure");
            if(this.assignedDevice == null) {
                logger.info("Device not assigned");
                if(logitowTag.hasKey("structures")) {
                    logger.info("Has key");
                    String name = logitowTag.getString("structures");
                    if(name == null || name == "NULL") {
                        logger.info("UUID is null");
                        this.assignedStructure = null;
                    } else {
                        logger.info("UUID is not null");
                        try {
                            logger.info("Loading structures for block {} ", getPos());
                            this.assignedStructure = Structure.loadByName(name);
                        } catch (IOException e) {
                            logger.error("Error loading structures for key block", e);
                            this.assignedStructure = null;
                        } finally {
                            if (this.assignedStructure != null) {
                                logger.info("Loaded structures from NBT: {}", this.assignedStructure);
                            } else {
                                this.markDirty();
                            }
                        }
                    }
                } else {
                    logger.info("Doesn't have key");
                    this.assignedStructure = null;
                }
            } else if(this.assignedStructure != this.assignedDevice.currentStructure) {
                logger.info("Device assigned");
                //Assign the current device's structures.
                this.assignedStructure = this.assignedDevice.currentStructure;
                this.markDirty();
            }

            //Player
            if(logitowTag.hasKey("player")) {
                String uuid = logitowTag.getString("player");
                if(uuid == null) {
                    this.assignedPlayer = null;
                } else {
                    try {
                        this.assignedPlayer = getWorld().getPlayerEntityByUUID(UUID.fromString(uuid));
                    } catch (Exception e) {
                        this.assignedPlayer = null;
                    } finally {
                        if(this.assignedPlayer == null) {
                            this.markDirty();
                        }
                    }
                }
            } else {
                this.assignedPlayer = null;
            }

            super.readFromNBT(compound);
        }
    }

    /**
     * Gets the device assigned to this block.
     * NULL if device not connected or nothing assigned.
     * @return
     */
    public Device getAssignedDevice() {
        return assignedDevice;
    }

    /**
     * Gets the player assigned to this key block.
     * NULL if device not connected or nothing assigned.
     * @return
     */
    public EntityPlayer getAssignedPlayer() {
        return assignedPlayer;
    }

    /**
     * Gets the structures assigned to this key block.
     * @return
     */
    public Structure getAssignedStructure() {
        return assignedStructure;
    }

    /**
     * Assigns a LOGITOW device to this key block.
     * @param player
     * @param device
     */
    public void assignDevice(EntityPlayer player,Device device) {
        if(player != null && device != null) {
            if(this.assignedDevice == null || !this.assignedDevice.equals(device) || this.assignedPlayer == null || !this.assignedPlayer.equals(player) || this.assignedStructure == null || !this.assignedStructure.equals(device.currentStructure)) {
                this.assignedDevice = device;
                this.assignedPlayer = player;
                clearStructure();
                if(this.assignedStructure != null && this.assignedStructure.customName == null) {
                    //Deleting the structure file.
                    Structure.removeFile(this.assignedStructure);
                }
                this.assignedStructure = device.currentStructure;
                rebuildStructure();
                logger.info("Assigned device: {} to key block at: {}", device, this.getPos());
                this.markDirty();
            }
        } else {
            this.assignedDevice = null;
            logger.info("Unassigned device from key block at: {}", this.getPos());
            this.markDirty();
        }
    }

    /**
     * Assigns the given structures to this key block.
     * @param structure
     */
    public void assignStructure(Structure structure) {
        if(structure != null) {
            clearStructure();
            if(assignedDevice != null || assignedPlayer != null) {
                assignDevice(null, null);
            }
            this.assignedStructure = structure;
            rebuildStructure();
        } else {
            clearStructure();
            this.assignedStructure = null;
        }

        this.markDirty();
    }

    /**
     * Rotates the structures assigned to this key block.
     */
    public boolean rotateStructure(EntityPlayer player, EnumFacing facing)
    {
        if (getWorld().isRemote)return false;

        //Getting the current structures.
        Structure current = getAssignedStructure();
        if(current == null) {
            player.sendMessage(TEXT_CANT_ROTATE_NOT_ATTACHED);
            return false;
        }

        //Getting the rotation to apply.
        Vec3 rotation = Vec3.zero();
        switch(facing) {
            case UP:
                rotation = new Vec3(0,90,0);
                break;
            case DOWN:
                rotation = new Vec3(0,-90,0);
                break;
            case NORTH:
                rotation = new Vec3(0,0,90);
                break;
            case SOUTH:
                rotation = new Vec3(0,0,-90);
                break;
            case EAST:
                rotation = new Vec3(90,0,0);
                break;
            case WEST:
                rotation = new Vec3(-90,0,0);
                break;
        }

        System.out.println("Rotating LOGITOW base block: " + this.getPos() + " by " + rotation);

        clearStructure();
        assignedStructure.rotate(rotation);
        rebuildStructure();
        player.sendMessage(new TextComponentTranslation(TEXT_ROTATED, rotation));

        return true;
    }
    /**
     * Called when the structures data is updated from the assigned device.
     * Called on both client and server.
     * @param event
     */
    public void onStructureUpdate(BlockOperationEvent event) {
        logger.info("Handling block update on key block: {} ", getPos());
        BlockOperation operation = event.operation;

        //No need to recreate the structures each time. Just adding the one updated block.
        //Getting the affected position.
        BlockPos affpos = getPos().add(operation.blockB.coordinate.getX(),operation.blockB.coordinate.getY(),operation.blockB.coordinate.getZ());

        if(getWorld().isRemote) {
            if(operation.operationType == BlockOperationType.BLOCK_ADD) {
                //Block added.
                BlockType blockType = operation.blockB.getBlockType();
                Block colour = BlockBase.getBlockFromName("logimine:"+blockType.name().toLowerCase()+"_lblock");
                Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(affpos, colour.getDefaultState());

                //Checking the end block.
                if(blockType == BlockType.END) {
                    //Showing the save gui.
                    Minecraft.getMinecraft().displayGuiScreen(new SaveStructureGui(this));
                }
            } else {
                //Block removed.
                IBlockState state = getWorld().getBlockState(affpos);
                Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(affpos, state);
            }
        } else {
            if(operation.operationType == BlockOperationType.BLOCK_ADD) {
                //Block added.
                Block colour = BlockBase.getBlockFromName("logimine:"+operation.blockB.getBlockType().name().toLowerCase()+"_lblock");
                getWorld().setBlockState(affpos, colour.getDefaultState());
            } else {
                //Block removed.
                getWorld().setBlockToAir(affpos);
            }
        }


    }

    /**
     * Clears the current structures.
     */
    public void clearStructure() {
        if(getWorld() == null) return;
        if (getWorld().isRemote) return;
        if(this.assignedStructure == null) return;

        logger.info("Clearing structures: {} on: {}", assignedStructure, getPos());

        //Removing all the old blocks.
        for (int i = 0; i < this.assignedStructure.blocks.size(); i++) {
            com.logitow.bridge.build.block.Block b = this.assignedStructure.blocks.get(i);
            if(b==null) continue;
            if(b.coordinate.equals(Vec3.zero())) continue;

            BlockPos removePosition = this.getPos().add(b.coordinate.getX(), b.coordinate.getY(), b.coordinate.getZ());

            getWorld().setBlockToAir(removePosition);
        }
    }
    /**
     * Rebuilds the current structures.
     */
    public void rebuildStructure() {
        if (getWorld().isRemote)return;

        logger.info("Rebuilding structures: {} on: {}", assignedStructure, getPos());

        //Placing all the new blocks.
        for (com.logitow.bridge.build.block.Block b :
                assignedStructure.blocks) {
            //Block added.
            if(!b.coordinate.equals(Vec3.zero())) {
                //Getting the affected position.
                BlockPos affpos = this.getPos().add(b.coordinate.getX(),b.coordinate.getY(),b.coordinate.getZ());
                System.out.println("Placing block: " + b + " at: " + affpos);

                Block colour;
                if(b.getBlockType() == BlockType.BASE) {
                    colour = BlockBase.getBlockFromName("logimine:"+"white"+"_lblock");
                } else {
                    colour = BlockBase.getBlockFromName("logimine:"+b.getBlockType().name().toLowerCase()+"_lblock");
                }
                getWorld().setBlockState(affpos,colour.getDefaultState());
            }
        }
    }
    /**
     * Checks whether the given player has permissions to this key block.
     * @param player
     * @return
     */
    public boolean checkPermissions(EntityPlayer player) {
        if(getAssignedPlayer() == null || player.getUniqueID() == getAssignedPlayer().getUniqueID()) {
            return true;
        }
        return false;
    }

    /**
     * Called when the world is being saved.
     */
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save saveEvent) {
        if(saveEvent.getWorld().isRemote) return;

        logger.info("Saving the current structures...");

        for (TileEntityBlockKey keyBlock :
                LogiMine.activeKeyBlocks) {
            //Saving the current structures to file.
            try {
                if(keyBlock.getWorld().isRemote) continue;
                if(keyBlock.assignedStructure != null) {
                    keyBlock.assignedStructure.saveToFile();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * Called when the world is unloaded.
     */
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        for (int i = 0; i < LogiMine.activeKeyBlocks.size(); i++) {
            if (LogiMine.activeKeyBlocks.get(i).getWorld().equals(event.getWorld())) {
                LogiMine.activeKeyBlocks.remove(i);
            }
        }
    }
}
