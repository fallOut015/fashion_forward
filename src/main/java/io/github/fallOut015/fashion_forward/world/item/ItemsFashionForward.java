package io.github.fallOut015.fashion_forward.world.item;

import com.sun.jna.Structure;
import io.github.fallOut015.fashion_forward.MainFashionForward;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsFashionForward {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MainFashionForward.MODID);

    public static final RegistryObject<Item> TOP_HAT = ITEMS.register("top_hat", () -> new WearableItem(EquipmentSlot.HEAD, 3, new Item.Properties().tab(CreativeModeTabFashionForward.TAB_FASHION_FORWARD)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}