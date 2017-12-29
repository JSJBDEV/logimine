package com.logitow.logimine.Blocks;

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
import net.minecraft.world.World;
import com.logitow.logimine.Items.ModItems;

import java.util.Random;

/**
 * Created by James on 14/12/2017.
 */
public class BlockKey extends BlockBase {

    public BlockKey(String name)
    {
        super(Material.WOOD,name);
    }
    @Override
    public BlockKey setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

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
    @Override
    public Item getItemDropped(IBlockState p_getItemDropped_1_, Random p_getItemDropped_2_, int p_getItemDropped_3_) {
        return Item.getItemFromBlock(ModBlocks.key_lblock);
    }
}
