package com.logitow.logimine.Blocks;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.logimine.Items.ModItems;
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

                if (stack.getItem() == ModItems.logiCard && player.isSneaking() )
                {
                    NBTTagCompound card = stack.getTagCompound();
                    card.setInteger("xpos", blockpos.getX());
                    card.setInteger("ypos", blockpos.getY());
                    card.setInteger("zpos", blockpos.getZ());
                }

                if (stack.getItem() == ModItems.logiCard && !player.isSneaking())
                {
                    direction(stack, player, world, blockpos);
                }
            }else
                {
                    NBTTagCompound nbt = new NBTTagCompound();
                    stack.setTagCompound(nbt);
                }
        }
        return super.onBlockActivated(world, blockpos, p_onBlockActivated_3_,player,hand, p_onBlockActivated_6_, p_onBlockActivated_7_, p_onBlockActivated_8_, p_onBlockActivated_9_);
    }

    /**
     * Called when the structure data is updated from the assigned device.
     * @param event
     */
    public void onStructureUpdate(BlockOperationEvent event) {
        System.out.println("Handling block update on key block " + position);
        BlockOperation operation = event.operation;

        //No need to recreate the structure each time. Just adding the one updated block.
        //Getting the affected position.
        BlockPos affpos = position.add(operation.blockB.coordinate.x,operation.blockB.coordinate.y,operation.blockB.coordinate.z);

        if(operation.operationType == BlockOperationType.BLOCK_ADD) {
            //Block added.
            Block colour = BlockBase.getBlockFromName("logimine:"+operation.blockB.getBlockType().name().toLowerCase()+"_lblock");
            Minecraft.getMinecraft().world.setBlockState(affpos,colour.getDefaultState());
        } else {
            //Block removed.
            Minecraft.getMinecraft().world.setBlockToAir(affpos);
        }
    }
    public void onStructureUpdate(Structure structure) {
        System.out.println("Handling structure update on key block " + position);

        for (com.logitow.bridge.build.block.Block b :
                structure.blocks) {
            //Getting the affected position.
            BlockPos affpos = position.add(b.coordinate.x,b.coordinate.y,b.coordinate.z);

            //Block added.
            Block colour = BlockBase.getBlockFromName("logimine:"+b.getBlockType().name().toLowerCase()+"_lblock");
            Minecraft.getMinecraft().world.setBlockState(affpos,colour.getDefaultState());
        }
    }

    /**
     * Frankly i have yet to figure out what this does.
     * @param stack
     * @param player
     * @param world
     * @param blockpos
     */
    public void direction(ItemStack stack,EntityPlayer player, World world,BlockPos blockpos)
    {
        NBTTagCompound nbt;
        nbt = stack.getTagCompound();
        try
        {
            switch(nbt.getInteger("dir"))
            {
                case 0:
                    nbt.setInteger("dir",1);
                    player.sendMessage(new TextComponentString("dir 2"));

                    world.setBlockToAir(blockpos.up());
                    world.setBlockState(blockpos.down(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 1:
                    nbt.setInteger("dir",2);
                    player.sendMessage(new TextComponentString("dir 3"));

                    world.setBlockToAir(blockpos.down());
                    world.setBlockState(blockpos.east(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 2:
                    nbt.setInteger("dir",3);
                    player.sendMessage(new TextComponentString("dir 4"));

                    world.setBlockToAir(blockpos.east());
                    world.setBlockState(blockpos.west(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 3:
                    nbt.setInteger("dir",4);
                    player.sendMessage(new TextComponentString("dir 5"));

                    world.setBlockToAir(blockpos.west());
                    world.setBlockState(blockpos.north(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 4:
                    nbt.setInteger("dir",5);
                    player.sendMessage(new TextComponentString("dir 6"));

                    world.setBlockToAir(blockpos.north());
                    world.setBlockState(blockpos.south(),ModBlocks.white_lblock.getDefaultState());
                    break;
                case 5:
                    nbt.setInteger("dir",0);
                    player.sendMessage(new TextComponentString("dir 1"));

                    world.setBlockToAir(blockpos.south());
                    world.setBlockState(blockpos.up(),ModBlocks.white_lblock.getDefaultState());
                    break;

            }
        } catch(Exception e){nbt.setInteger("dir",0);}

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
