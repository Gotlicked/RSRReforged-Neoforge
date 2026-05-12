package com.gotlicked.rsrreforged.block.entity;

import com.gotlicked.rsrreforged.RSRReforged;
import com.gotlicked.rsrreforged.block.CraftingEmitterBlock;
import com.gotlicked.rsrreforged.block.RSRRBlocks;
import com.gotlicked.rsrreforged.block.RequesterBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RSRRBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, RSRReforged.MOD_ID);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public static Supplier<BlockEntityType<RequesterBlockEntity>> REQUESTER_BE_TYPE = BLOCK_ENTITY_TYPES.register(
            "requester",
            () -> new BlockEntityType<>(
                    ((RequesterBlock) RSRRBlocks.REQUESTER.get()).getTypedFactory(),
                    RSRRBlocks.REQUESTER.get()));

    public static Supplier<BlockEntityType<CraftingEmitterBlockEntity>> CRAFTING_EMITTER_BE_TYPE
            = BLOCK_ENTITY_TYPES.register(
            "crafting_emitter",
            () -> new BlockEntityType<>(
                    ((CraftingEmitterBlock) RSRRBlocks.CRAFTING_EMITTER.get()).getTypedFactory(),
                    RSRRBlocks.CRAFTING_EMITTER.get()));


}
