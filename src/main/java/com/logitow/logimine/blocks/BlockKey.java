package com.logitow.logimine.blocks;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.build.Vec3;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.build.block.BlockType;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.logimine.items.ModItems;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.client.gui.DeviceManagerGui;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by James on 14/12/2017.
 * Modified by itsMatoosh on 05/01/2017.
 */
public class BlockKey extends BlockBase {

    private BlockPos position;

    /**
     * Creates a base block with a certain name.
     * @param name
     */
    public BlockKey(String name)
    {
        super(Material.WOOD,name);
    }

    /**
     * Sets the creative mode tab for the block.
     * @param tab
     * @return
     */
    @Override
    public BlockKey setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }



    /**
     * Called when the player right clicks the block.
     * @param world
     * @param blockpos
     * @param p_onBlockActivated_3_
     * @param player
     * @param hand
     * @param p_onBlockActivated_6_
     * @param p_onBlockActivated_7_
     * @param p_onBlockActivated_8_
     * @param p_onBlockActivated_9_
     * @return
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos blockpos, IBlockState p_onBlockActivated_3_, EntityPlayer player, EnumHand hand, EnumFacing p_onBlockActivated_6_, float p_onBlockActivated_7_, float p_onBlockActivated_8_, float p_onBlockActivated_9_)
    {
        this.position = blockpos;
        if(player.getHeldItem(hand) != ItemStack.EMPTY)
        {
            ItemStack stack = player.getHeldItem(hand);
            if(stack.hasTagCompound())
            {
                if(stack.getItem() == ModItems.logiCard) {
                    if (player.isSneaking() )
                    {
                        //Rotating the structure.
                        direction(stack, player, world, blockpos, p_onBlockActivated_6_);
                    }
                    else
                    {
                        Minecraft.getMinecraft().displayGuiScreen(new DeviceManagerGui());
                        //Assigning the block to the device manager dialog.
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        DeviceManagerGui.onKeyBlockAssigned(BlockKey.this);
                                    }
                                },
                                500
                        );
                    }
                }
            }
            else
            {
                if (world.isRemote){ //Running on server only.
                    return false;
                }
                NBTTagCompound nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
        }
        return true;
    }

    /**
     * Called when the structure data is updated from the assigned device.
     * Called on client only.
     * @param event
     */
    public void onStructureUpdate(BlockOperationEvent event) {
        //Making sure the update doesn't happen when the player is not in the world.
        if (Minecraft.getMinecraft().world == null)  {
            getAssignedDevice().disconnect();
            return;
        }

        System.out.println("Handling block update on key block " + position);
        BlockOperation operation = event.operation;

        //No need to recreate the structure each time. Just adding the one updated block.
        //Getting the affected position.
        BlockPos affpos = position.add(operation.blockB.coordinate.x,operation.blockB.coordinate.y,operation.blockB.coordinate.z);

        if(operation.operationType == BlockOperationType.BLOCK_ADD) {
            //Block added.
            Block colour = BlockBase.getBlockFromName("logimine:"+operation.blockB.getBlockType().name().toLowerCase()+"_lblock");
            Minecraft.getMinecraft().world.setBlockState(affpos, colour.getDefaultState());
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(affpos, colour.getDefaultState());
        } else {
            //Block removed.
            IBlockState aimState = Minecraft.getMinecraft().world.getBlockState(affpos);
            Minecraft.getMinecraft().world.setBlockToAir(affpos);
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(affpos, aimState);
        }

        //TODO: Send update packet to server.
    }

    /**
     * Clears the current structure.
     * @param structure
     */
    public void clearStructure(Structure structure) {
        //Removing all the old blocks.
        for (int i = 0; i < structure.blocks.size(); i++) {
            com.logitow.bridge.build.block.Block b = structure.blocks.get(i);
            if(b==null) continue;
            if(b.getBlockType() == BlockType.BASE) continue;

            BlockPos removePosition = this.position.add(b.coordinate.x, b.coordinate.y, b.coordinate.z);

            Minecraft.getMinecraft().world.setBlockToAir(removePosition);
        }
    }
    /**
     * Rebuilds the structure.
     */
    public void rebuildStructure(Structure structure) {
        System.out.println("Rebuilding the structure: " + structure + " on: " + position);

        //Placing all the new blocks.
        for (com.logitow.bridge.build.block.Block b :
                structure.blocks) {
            //Block added.
            if(b.getBlockType() != BlockType.BASE) {
                //Getting the affected position.
                BlockPos affpos = this.position.add(b.coordinate.x,b.coordinate.y,b.coordinate.z);
                System.out.println("Placing block: " + b + " at: " + affpos);

                Block colour = BlockBase.getBlockFromName("logimine:"+b.getBlockType().name().toLowerCase()+"_lblock");
                Minecraft.getMinecraft().world.setBlockState(affpos,colour.getDefaultState());
            }
        }
    }

    /**
     * Frankly i have yet to figure out what this does.
     * @param stack
     * @param player
     * @param world
     * @param blockpos
     */
    public void direction(ItemStack stack,EntityPlayer player, World world,BlockPos blockpos, EnumFacing facing)
    {
        //Run on server only.
        if (world.isRemote){
            return;
        }

        NBTTagCompound nbt;
        nbt = stack.getTagCompound();
        Device assigned = getAssignedDevice();
        if(assigned == null) {
        player.sendMessage(new TextComponentString("Can't rotate the LOGITOW base. No structure attached!"));
            return;
        }
        try
        {
            //The currently applied rotation.
            int[] currentRotation = nbt.getIntArray("rot");

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

            System.out.println("Rotating LOGITOW base block: " + position + " by " + rotation);

            //Increasing current rot.
            currentRotation[0] += rotation.x;
            currentRotation[1] += rotation.y;
            currentRotation[2] += rotation.z;

            //TODO: Position second base block.
            /*switch(currentRotation)
            {
                case 0:
                    world.setBlockToAir(blockpos.up());
                    world.setBlockState(blockpos.down(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 1:
                    world.setBlockToAir(blockpos.down());
                    world.setBlockState(blockpos.east(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 2:
                    world.setBlockToAir(blockpos.east());
                    world.setBlockState(blockpos.west(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 3:
                    world.setBlockToAir(blockpos.west());
                    world.setBlockState(blockpos.north(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 4:
                    world.setBlockToAir(blockpos.north());
                    world.setBlockState(blockpos.south(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 5:
                    world.setBlockToAir(blockpos.south());
                    world.setBlockState(blockpos.up(),ModBlocks.white_lblock.getDefaultState());
                    break;

            }*/

            nbt.setIntArray("rot", currentRotation);
            clearStructure(assigned.currentStructure);
            assigned.currentStructure.rotate(rotation);
            rebuildStructure(getAssignedDevice().currentStructure);
            player.sendMessage(new TextComponentString("Rotated structure by: " + rotation));
        } catch(Exception e){nbt.setIntArray("rot",new int[] {0,0,0});}
    }

    /**
     * Gets the device assigned to this block.
     * @return
     */
    public Device getAssignedDevice() {
        for (String uuid :
                LogiMine.assignedDevices.keySet()) {
            if(LogiMine.assignedDevices.get(uuid).position == this.position) {
                return Device.getConnectedFromUuid(uuid);
            }
        }
        return null;
    }

    /**
     * Gets a dropped instance of the item.
     * @param p_getItemDropped_1_
     * @param p_getItemDropped_2_
     * @param p_getItemDropped_3_
     * @return
     */
    @Override
    public Item getItemDropped(IBlockState p_getItemDropped_1_, Random p_getItemDropped_2_, int p_getItemDropped_3_) {
        return Item.getItemFromBlock(ModBlocks.key_lblock);
    }
}