package com.logitow.logimine.items;

import com.logitow.bridge.communication.BluetoothState;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.logimine.LogiMine;
import com.logitow.logimine.client.gui.BluetoothDialogGui;
import com.logitow.logimine.client.gui.DeviceManagerGui;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


/**
 * Created by James on 14/12/2017.
 */
public class ItemLogiCard extends Item {
    protected String name;

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

    /**
     * Called when the equipped item is right clicked.
     *
     * @param worldIn
     * @param playerIn
     * @param handIn
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        //Display only on client.
        if(!worldIn.isRemote) return super.onItemRightClick(worldIn, playerIn, handIn);

        //Cancel if sneaking.
        if(!playerIn.isSneaking()) {
            //Checking for bluetooth availability.
            if(LogitowDeviceManager.current.getBluetoothState() == BluetoothState.PoweredOn) {
                //Opening the device manager screen.
                Minecraft.getMinecraft().displayGuiScreen(new DeviceManagerGui());
            } else {
                //Showing ble unavailable dialog.
                Minecraft.getMinecraft().displayGuiScreen(new BluetoothDialogGui(LogitowDeviceManager.current.getBluetoothState()));
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
