package com.logitow.logimine;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import com.logitow.logimine.blocks.ModBlocks;

/**
 * Created by James on 17/12/2017.
 */
public class ModTab extends CreativeTabs {
    public ModTab() {
        super(LogiMine.modId);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.key_lblock);
    }

}
