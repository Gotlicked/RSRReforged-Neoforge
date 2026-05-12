package com.gotlicked.rsrreforged.item;

import com.gotlicked.rsrreforged.RSRReforged;
import com.gotlicked.rsrreforged.block.RSRRBlocks;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RSRRItems {


    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RSRReforged.MOD_ID);

    public static final Supplier<BlockItem> REQUESTER_ITEM = ITEMS.registerSimpleBlockItem(
            "requester",
            RSRRBlocks.REQUESTER,
            props
                    -> props);

    public static final Supplier<BlockItem> CRAFTING_EMITTER_ITEM = ITEMS.registerSimpleBlockItem(
            "crafting_emitter",
            RSRRBlocks.CRAFTING_EMITTER,
            props
                    -> props);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
