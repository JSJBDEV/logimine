package com.logitow.logimine.Blocks;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.build.block.BlockSide;
import com.logitow.bridge.build.block.BlockType;
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

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by James on 14/12/2017.
 * Modified by itsMatoosh on 05/01/2017.
 */
public class BlockKey extends BlockBase {

    private BlockPos position;

    /**
     * All the blocks attached to this key block.
     */
    private ArrayList<BlockPos> blocks = new ArrayList<>();

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
                        direction(stack, player, world, blockpos);
                    }
                    else
                    {
                        //Assigning the block to the device manager dialog.
                        Minecraft.getMinecraft().displayGuiScreen(new DeviceManagerGui());
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
            }else
                {
                    NBTTagCompound nbt = new NBTTagCompound();
                    stack.setTagCompound(nbt);
                }
        }
        return true;
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
            blocks.add(affpos);
            Minecraft.getMinecraft().world.setBlockState(affpos,colour.getDefaultState());
        } else {
            //Block removed.
            Minecraft.getMinecraft().world.setBlockToAir(affpos);
            for (int i = 0; i < blocks.size(); i++) {
                BlockPos pos = blocks.get(i);
                if(pos != null && pos==affpos) {
                    blocks.remove(pos);
                }
            }
        }
    }

    /**
     * Rebuilds the structure.
     */
    public void rebuildStructure(Structure structure) {
        System.out.println("Rebuilding the structure: " + structure + " on: " + position);

        //Removing all the old blocks.
        for (int i = 0; i < blocks.size(); i++) {
            BlockPos pos = blocks.get(i);
            if(pos==null) continue;

            Minecraft.getMinecraft().world.setBlockToAir(pos);
            blocks.remove(pos);
        }

        //Placing all the new blocks.
        for (com.logitow.bridge.build.block.Block b :
                structure.blocks) {
            //Block added.
            if(b.getBlockType() != BlockType.BASE) {
                //Getting the affected position.
                BlockPos affpos = this.position.add(b.coordinate.x,b.coordinate.y,b.coordinate.z);
                System.out.println("Placing block: " + b + " at: " + affpos);
                blocks.add(affpos);

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
    public void direction(ItemStack stack,EntityPlayer player, World world,BlockPos blockpos)
    {
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
            int currentRotation = nbt.getInteger("dir");
            System.out.println("Rotating LOGITOW base block " + position + " to " + (currentRotation +1));

            switch(currentRotation)
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

            }

            int newRotation = currentRotation+1;
            if(newRotation > 5) {
                newRotation = 0;
            }

            nbt.setInteger("dir", newRotation);
            assigned.currentStructure.rotate(BlockSide.getBlockSide(newRotation+1));
            rebuildStructure(getAssignedDevice().currentStructure);
            player.sendMessage(new TextComponentString("Rotated structure to direction: " + newRotation));
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
