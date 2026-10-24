package com.example.handmod;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item SCALPEL = register("scalpel", new ScalpelItem(new Item.Settings().maxCount(1)));
    public static final Item NEEDLE = register("needle", new NeedleItem(new Item.Settings().maxCount(1)));
    public static final Item SEVERED_ARM = register("severed_arm", new Item(new Item.Settings().maxCount(16)));

    private ModItems() {}

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(HandMod.MOD_ID, name), item);
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SCALPEL);
            entries.add(NEEDLE);
            entries.add(SEVERED_ARM);
        });
    }
}
