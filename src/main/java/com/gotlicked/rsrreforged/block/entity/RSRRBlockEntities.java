package com.gotlicked.rsrreforged.block.entity;

import com.gotlicked.rsrreforged.RSRReforged;
import com.gotlicked.rsrreforged.block.CraftingEmitterBlock;
import com.gotlicked.rsrreforged.block.RSRRBlocks;
import com.gotlicked.rsrreforged.block.RequesterBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RSRRBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            RSRReforged.MOD_ID);

    @SuppressWarnings("unchecked")
    public static DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<? extends RequesterBlockEntity>> REQUESTER_BE_TYPE = BLOCK_ENTITY_TYPES.register("requester", () -> new BlockEntityType<>((
            BlockEntityType.BlockEntitySupplier<? extends RequesterBlockEntity>) ((RequesterBlock) RSRRBlocks.REQUESTER.get()).getTileEntityFactory(), RSRRBlocks.REQUESTER.get()));
    @SuppressWarnings("unchecked")
    public static DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<? extends CraftingEmitterBlockEntity>> CRAFTING_EMITTER_BE_TYPE = BLOCK_ENTITY_TYPES.register("crafting_emitter", () -> new BlockEntityType<>((
            BlockEntityType.BlockEntitySupplier<? extends CraftingEmitterBlockEntity>) ((CraftingEmitterBlock) RSRRBlocks.CRAFTING_EMITTER.get()).getTileEntityFactory(), RSRRBlocks.CRAFTING_EMITTER.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

}
