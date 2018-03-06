package com.logitow.logimine.items;

import com.logitow.logimine.LogiMine;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;


/**
 * Created by James on 14/12/2017.
 */
public class ModItems {
    public static ItemLogiCard logiCard = new ItemLogiCard("logicard").setCreativeTab(LogiMine.tab);


    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                logiCard
        );
    }


    public static void registerModels() {
        logiCard.registerItemModel();
    }

}
