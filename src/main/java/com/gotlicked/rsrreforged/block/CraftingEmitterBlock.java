package com.gotlicked.rsrreforged.block;

import com.gotlicked.rsrreforged.block.entity.CraftingEmitterBlockEntity;
import com.gotlicked.rsrreforged.block.entity.RSRRBlockEntities;
import com.refinedmods.refinedstorage.common.support.AbstractBaseBlock;
import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class CraftingEmitterBlock extends AbstractBaseBlock implements EntityBlock {

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 5, 16);
    @SuppressWarnings("unchecked")
    private static final AbstractBlockEntityTicker<CraftingEmitterBlockEntity> TICKER = new NetworkNodeBlockEntityTicker<>(
            () -> (BlockEntityType<CraftingEmitterBlockEntity>) RSRRBlockEntities.CRAFTING_EMITTER_BE_TYPE.get(),
            null
    );
    public static BooleanProperty POWERED = BooleanProperty.create("powered");

    public CraftingEmitterBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("rsrreforged", "crafting_emitter"))));
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) -> new CraftingEmitterBlockEntity(RSRRBlockEntities.CRAFTING_EMITTER_BE_TYPE.get(), blockPos, blockState);
    }

    @Override
    public @Nullable <R extends BlockEntity> BlockEntityTicker<R> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<R> entityType) {
        return TICKER.get(level, entityType);
    }

    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return this.getTileEntityFactory().create(pos, state);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter worldIn, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isSignalSource(@NonNull BlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public int getDirectSignal(@NonNull BlockState blockState, BlockGetter blockAccess, @NonNull BlockPos pos, @NonNull Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof CraftingEmitterBlockEntity craftingEmitterBlockEntity) {
            if (craftingEmitterBlockEntity.getShouldEmmitRedstone()) return 15;
        }
        return 0;
    }

    @Override
    public int getSignal(@NonNull BlockState blockState, BlockGetter blockAccess, @NonNull BlockPos pos, @NonNull Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof CraftingEmitterBlockEntity craftingEmitterBlockEntity) {
            if (craftingEmitterBlockEntity.getShouldEmmitRedstone()) return 15;
        }
        return 0;
    }
}
