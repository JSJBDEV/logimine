package com.logitow.logimine.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import com.logitow.logimine.LogiMine;

import java.util.Random;

/**
 * Created by James on 14/12/2017.
 */
public class BlockBase extends Block {
    protected String name;

    public BlockBase(Material material, String name) {
        super(material);

        this.name = name;
        this.setHardness(0f);
        setUnlocalizedName(name);
        setRegistryName(name);
    }

    public void registerItemModel(Item itemBlock) {
        LogiMine.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

    @Override
    public Item getItemDropped(IBlockState p_getItemDropped_1_, Random p_getItemDropped_2_, int p_getItemDropped_3_) {
        return null;
    }

}
