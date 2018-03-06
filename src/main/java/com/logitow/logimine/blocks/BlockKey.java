package com.logitow.logimine.blocks;

import com.logitow.logimine.client.gui.DeviceManagerGui;
import com.logitow.logimine.items.ModItems;
import com.logitow.logimine.tiles.TileEntityBlockKey;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by James on 14/12/2017.
 * Modified by itsMatoosh on 05/01/2017.
 */
public class BlockKey extends BlockBase implements ITileEntityProvider {
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
     * @param facing
     * @param p_onBlockActivated_7_
     * @param p_onBlockActivated_8_
     * @param p_onBlockActivated_9_
     * @return
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos blockpos, IBlockState p_onBlockActivated_3_, EntityPlayer player, EnumHand hand, EnumFacing facing, float p_onBlockActivated_7_, float p_onBlockActivated_8_, float p_onBlockActivated_9_)
    {
        //Getting the block's tile entity.
        TileEntity te = world.getTileEntity(blockpos);
        if(te != null && te instanceof TileEntityBlockKey) {
            TileEntityBlockKey blockKeyEntity = (TileEntityBlockKey) te;

            if(blockKeyEntity.checkPermissions(player)) {
                //Handling interaction.
                //Making sure the player is holding the logicard.
                if(player.getHeldItem(hand) != ItemStack.EMPTY) {
                    ItemStack stack = player.getHeldItem(hand);
                    if (stack.getItem() == ModItems.logiCard) {
                        if (player.isSneaking()) {
                            //Rotating the structure.
                            return blockKeyEntity.rotateStructure(player, facing);
                        } else if(world.isRemote) {
                            Minecraft.getMinecraft().displayGuiScreen(new DeviceManagerGui());
                            //Assigning the block to the device manager dialog.
                            DeviceManagerGui.onKeyBlockAssigned(blockpos);
                            return true;
                        }
                    }
                }
            } else {
                player.sendMessage(new TextComponentString("Another player is currently using this key block!"));
                return false;
            }
        }
        return false;
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) { //Client
            //Closing the gui.
            if(DeviceManagerGui.instance != null) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }

        //Unassigning the block.
        TileEntity te = worldIn.getTileEntity(pos);
        if(te != null && te instanceof TileEntityBlockKey) {
            TileEntityBlockKey blockKeyEntity = (TileEntityBlockKey) te;
            blockKeyEntity.assignDevice(null, null);
        }
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

    /**
     * Creates a tile entity from this block.
     * @param worldIn
     * @param meta
     * @return
     */
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBlockKey();
    }
}
