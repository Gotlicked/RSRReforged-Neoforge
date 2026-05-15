package com.gotlicked.rsrreforged.block;

import com.gotlicked.rsrreforged.RSRReforged;
import com.gotlicked.rsrreforged.block.entity.RequesterBlockEntity;
import com.refinedmods.refinedstorage.common.support.AbstractBaseBlock;
import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class RequesterBlock extends AbstractBaseBlock implements EntityBlock {

    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    private static final AbstractBlockEntityTicker<RequesterBlockEntity> TICKER =
            new NetworkNodeBlockEntityTicker<>(
                    RequesterBlock::requesterType, CONNECTED);

    public RequesterBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(ResourceKey.create(
                Registries.BLOCK,
                Identifier.fromNamespaceAndPath(
                        "rsrreforged", "requester"))));
        registerDefaultState(getStateDefinition().any().setValue(CONNECTED, false));
    }

    private static BlockEntityType<RequesterBlockEntity> requesterType() {
        return RSRReforged.RSRRBlockEntities.REQUESTER_BE_TYPE.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED);
    }

    public BlockEntityType.BlockEntitySupplier<RequesterBlockEntity> getTypedFactory() {
        return (blockPos, blockState) ->
                new RequesterBlockEntity(
                        RSRReforged.RSRRBlockEntities.REQUESTER_BE_TYPE.get(), blockPos, blockState);
    }

    @Override
    public <R extends BlockEntity> BlockEntityTicker<@NonNull R> getTicker(
            @NonNull Level level,
            @NonNull BlockState state,
            @NonNull BlockEntityType<R> entityType) {
        return TICKER.get(level, entityType);
    }

    @Nullable
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return getTypedFactory().create(pos, state);
    }

}
