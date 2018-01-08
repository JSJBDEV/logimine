package com.logitow.logimine.Blocks;

import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.EventHandler;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import com.logitow.logimine.LogiMine;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import com.logitow.logimine.Items.ModItems;

import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by James on 14/12/2017.
 * Modified by itsMatoosh on 05/01/2017.
 */
public class BlockKey extends BlockBase {

    /**
     * The device registered to this block base.
     */
    public Device device;

    /**
     * Whether a block operation has been received.
     */
    private boolean blockOperationReceived = false;
    /**
     * The latest received block operation.
     */
    private ArrayList<BlockOperation> latestOperations = new ArrayList<>();
    /**
     * The block operation event handler.
     */
    private EventHandler blockOperationHandler;

    /**
     * Creates a base block with a certain name.
     * TODO: Why is the name not hard-coded?
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
        if(player.getHeldItem(hand) != ItemStack.EMPTY)
        {
            ItemStack stack = player.getHeldItem(hand);
            if(stack.hasTagCompound())
            {
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
     * Called every tick.
     * @param world
     * @param blockPos
     * @param blockState
     * @param randomWithSeed
     */
    @Override
    public void updateTick(World world, BlockPos blockPos, IBlockState blockState, Random randomWithSeed)
    {
        //TODO: Move this to the event class.
        //Checking if the block has a device attached.
        if(device == null) {
            //Attaching device from unassigned.
            Device d = LogiMine.unassignedDevices.get(0);
            assignDevice(d);
            LogiMine.unassignedDevices.remove(d);
        }

        //Checking if an update happened since last tick.
        if(blockOperationReceived) {
            blockOperationReceived = false;

            //Enumeration operations.
            BlockOperation[] operations = (BlockOperation[])latestOperations.toArray();
            for (BlockOperation operation : operations) {
                //No need to recreate the structure each time. Just adding the one updated block.
                //Getting the affected position.
                BlockPos affpos = blockPos.add(operation.blockB.coordinate.x,operation.blockB.coordinate.y,operation.blockB.coordinate.z);

                if(operation.operationType == BlockOperationType.BLOCK_ADD) {
                    //Block added.
                    Block colour = BlockBase.getBlockFromName("logimine:"+operation.blockB.getBlockType().name().toLowerCase()+"_lblock");
                    world.setBlockState(affpos,colour.getDefaultState());
                } else {
                    //Block removed.
                    world.setBlockToAir(affpos);
                }

                latestOperations.remove(operation);
            }
        }
        super.updateTick(world, blockPos, blockState, randomWithSeed);
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
     * Assigns a LOGITOW device to this base block.
     */
    public void assignDevice(Device device) {
        this.device = device;

        //Registering block operation events.
        blockOperationHandler = new EventHandler() {
            @Override
            public void onEventCalled(Event event) {
                //Called when a block operation takes place on the LOGITOW device.
                //Checking if the device is right.
                BlockOperationEvent operationEvent = (BlockOperationEvent)event;
                if(operationEvent.device.toString() == operationEvent.device.toString()) {
                    //Setting the operation flag and waiting for update to change the structure.
                    blockOperationReceived = true;
                    latestOperations.add(operationEvent.operation);
                }
            }
        };
        EventManager.registerHandler(blockOperationHandler, BlockOperationEvent.class);
    }

    /**
     * Called when destroyed by player.
     * @param p_onBlockDestroyedByPlayer_1_
     * @param p_onBlockDestroyedByPlayer_2_
     * @param p_onBlockDestroyedByPlayer_3_
     */
    @Override
    public void onBlockDestroyedByPlayer(World p_onBlockDestroyedByPlayer_1_, BlockPos p_onBlockDestroyedByPlayer_2_, IBlockState p_onBlockDestroyedByPlayer_3_) {
        this.device = null;
        EventManager.unregisterHandler(blockOperationHandler, BlockOperationEvent.class);
        super.onBlockDestroyedByPlayer(p_onBlockDestroyedByPlayer_1_, p_onBlockDestroyedByPlayer_2_, p_onBlockDestroyedByPlayer_3_);
    }

    /**
     * Called when exploded.
     * @param p_onBlockExploded_1_
     * @param p_onBlockExploded_2_
     * @param p_onBlockExploded_3_
     */
    @Override
    public void onBlockExploded(World p_onBlockExploded_1_, BlockPos p_onBlockExploded_2_, Explosion p_onBlockExploded_3_) {
        this.device = null;
        EventManager.unregisterHandler(blockOperationHandler, BlockOperationEvent.class);
        super.onBlockExploded(p_onBlockExploded_1_, p_onBlockExploded_2_, p_onBlockExploded_3_);
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
