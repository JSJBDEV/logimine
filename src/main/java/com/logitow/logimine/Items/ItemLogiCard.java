package com.logitow.logimine.Items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import com.logitow.logimine.Blocks.BlockBase;
import com.logitow.logimine.Blocks.ModBlocks;
import com.logitow.logimine.LogiMine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created by James on 14/12/2017.
 */
public class ItemLogiCard extends Item {
    protected String name;
    boolean itemBound = false;

    public ItemLogiCard(String name) {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
    }

    public void registerItemModel() {
        LogiMine.proxy.registerItemRenderer(this, 0, name);
    }

    @Override
    public ItemLogiCard setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity p_onUpdate_3_, int p_onUpdate_4_, boolean p_onUpdate_5_) {
        if(stack.hasTagCompound())
        {
            int x = stack.getTagCompound().getInteger("xpos");
            int y = stack.getTagCompound().getInteger("ypos");
            int z = stack.getTagCompound().getInteger("zpos");
            BlockPos pos = new BlockPos(x,y,z);

            if(world.getBlockState(pos) == ModBlocks.key_lblock.getDefaultState())
            {
                try{
                List<String> model = Files.readAllLines(Paths.get("model.txt")); //located in the run directory
                    for(int i =0; i<model.size(); i++)
                    {

                        String[] current = model.get(i).split(",");
                        Block colour = BlockBase.getBlockFromName("logimine:"+current[0]);

                        BlockPos relpos = new BlockPos(x+Integer.parseInt(current[1]),y+Integer.parseInt(current[2]),z+Integer.parseInt(current[3]));
                        world.setBlockState(relpos,colour.getDefaultState());

                    }
                }catch(Exception e){}
            }

        }
        super.onUpdate(stack, world, p_onUpdate_3_, p_onUpdate_4_, p_onUpdate_5_);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack p_doesSneakBypassUse_1_, IBlockAccess p_doesSneakBypassUse_2_, BlockPos p_doesSneakBypassUse_3_, EntityPlayer p_doesSneakBypassUse_4_) {
        return true;
    }
}
