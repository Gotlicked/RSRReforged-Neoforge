package com.gotlicked.rsrreforged.block;

import com.gotlicked.rsrreforged.RSRReforged;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RSRRBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(
            RSRReforged.MOD_ID
    );

    public static final Supplier<Block> REQUESTER = BLOCKS.register(
            "requester",
            RequesterBlock::new
    );
    public static final Supplier<Block> CRAFTING_EMITTER = BLOCKS.register(
            "crafting_emitter",
            CraftingEmitterBlock::new
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
