package com.logitow.logimine.Items;

import com.logitow.bridge.event.Event;
import com.logitow.bridge.event.EventHandler;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
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
    public boolean doesSneakBypassUse(ItemStack p_doesSneakBypassUse_1_, IBlockAccess p_doesSneakBypassUse_2_, BlockPos p_doesSneakBypassUse_3_, EntityPlayer p_doesSneakBypassUse_4_) {
        return true;
    }
}
