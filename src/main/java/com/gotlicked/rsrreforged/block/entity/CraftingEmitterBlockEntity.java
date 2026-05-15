package com.gotlicked.rsrreforged.block.entity;

import com.gotlicked.rsrreforged.RSRReforged;
import com.gotlicked.rsrreforged.block.CraftingEmitterBlock;
import com.gotlicked.rsrreforged.container.CraftingEmitterContainer;
import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceTransferResult;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.FilterWithFuzzyMode;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicators;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.network.SimpleConnectionStrategy;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.refinedmods.refinedstorage.common.util.PlatformUtil.enumStreamCodec;

public class CraftingEmitterBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<CraftingEmitterBlockEntity.CraftingEmitterNetworkNode>
        implements NetworkNodeExtendedMenuProvider<CraftingEmitterBlockEntity.CraftingEmitterData> {

    private static final int EXPORT_SLOTS = 9;

    private final FilterWithFuzzyMode filter;
    private boolean shouldEmmitRedstone;

    public CraftingEmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state,
                new CraftingEmitterNetworkNode(16));
        this.filter = FilterWithFuzzyMode.create(
                createFilterContainer(), this::setChanged);
        this.shouldEmmitRedstone = false;
        this.mainNetworkNode.setFilter(this.filter);
        this.mainNetworkNode.setShouldEmmitRedstone(this::updatePower);
    }

    public static ResourceContainer createFilterContainer() {
        return new ResourceContainerImpl(
                EXPORT_SLOTS, _ -> Integer.MAX_VALUE,
                RefinedStorageApi.INSTANCE.getItemResourceFactory(),
                RefinedStorageApi.INSTANCE.getAlternativeResourceFactories());
    }

    public static ResourceContainer createFilterContainer(final CraftingEmitterData interfaceData) {
        final ResourceContainer filterContainer = createFilterContainer();
        final ResourceContainerData resourceContainerData = interfaceData.filterContainerData();
        for (
                int i = 0; i < resourceContainerData.resources().size(); ++i) {
            final int ii = i;
            resourceContainerData.resources().get(i).ifPresent(
                    resource -> filterContainer.set(
                            ii, resource));
        }
        return filterContainer;
    }

    @Override
    public void setLevel(@NonNull Level level) {
        super.setLevel(level);
        this.mainNetworkNode.setLevel(level);
    }

    public void updatePower(boolean powered) {
        if (this.shouldEmmitRedstone != powered) {
            this.shouldEmmitRedstone = powered;
            assert this.level != null;
            this.level.setBlockAndUpdate(
                    this.worldPosition,
                    this.getBlockState().setValue(
                            CraftingEmitterBlock.POWERED, powered));
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(
            final int syncId, final @NonNull Inventory inventory,
            final @NonNull Player player) {
        return new CraftingEmitterContainer(
                syncId, player, this, filter.getFilterContainer(), getExportingIndicators());
    }


    @Override
    public @NonNull CraftingEmitterData getMenuData() {
        return new CraftingEmitterData(
                ResourceContainerData.of(
                        filter.getFilterContainer()),
                getExportingIndicators().getAll());
    }

    @Override
    public @NonNull StreamEncoder<RegistryFriendlyByteBuf, CraftingEmitterData> getMenuCodec() {
        return CraftingEmitterData.STREAM_CODEC;
    }


    private ExportingIndicators getExportingIndicators() {
        return new ExportingIndicators(
                filter.getFilterContainer(), i -> toExportingIndicator(
                mainNetworkNode.getLastResult(i)), true);
    }

    private ExportingIndicator toExportingIndicator(
            @Nullable final InterfaceTransferResult result) {
        return switch (result) {
            case STORAGE_DOES_NOT_ACCEPT_RESOURCE -> ExportingIndicator.DESTINATION_DOES_NOT_ACCEPT_RESOURCE;
            case RESOURCE_MISSING -> ExportingIndicator.RESOURCE_MISSING;
            case AUTOCRAFTING_STARTED -> ExportingIndicator.AUTOCRAFTING_WAS_STARTED;
            case AUTOCRAFTING_MISSING_RESOURCES -> ExportingIndicator.AUTOCRAFTING_MISSING_RESOURCES;
            case null, default -> ExportingIndicator.NONE;
        };
    }

    @Override
    public @NonNull Component getName() {
        return Component.translatable("block.rsrreforged.crafting_emitter");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    protected @NonNull InWorldNetworkNodeContainer createMainContainer(
            @NonNull CraftingEmitterNetworkNode networkNode) {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(
                this, networkNode).connectionStrategy(
                new SimpleConnectionStrategy(getBlockPos())).build();
    }

    @Override
    public void writeConfiguration(final @NonNull ValueOutput output) {
        super.writeConfiguration(output);
        filter.store(output);
    }

    @Override
    public void readConfiguration(final @NonNull ValueInput input) {
        super.readConfiguration(input);
        filter.read(input);
    }

    public boolean getShouldEmmitRedstone() {
        return shouldEmmitRedstone;
    }

    public static class CraftingEmitterNetworkNode extends SimpleNetworkNode {

        private FilterWithFuzzyMode filter;
        private Level level;
        private InterfaceTransferResult[] results;
        private Consumer<Boolean> shouldEmmitRedstone;

        public CraftingEmitterNetworkNode(long energyUsage) {
            super(energyUsage);
        }

        public void setFilter(FilterWithFuzzyMode filter) {
            this.filter = filter;
            this.results = new InterfaceTransferResult[filter.getFilterContainer().size()];
        }

        public void setLevel(Level level) {
            this.level = level;
        }

        public void setShouldEmmitRedstone(Consumer<Boolean> shouldEmmitRedstone) {
            this.shouldEmmitRedstone = shouldEmmitRedstone;
        }

        @Override
        public void doWork() {
            super.doWork();
            if (network == null || !isActive() || this.filter == null || this.level == null
                    || this.level.getGameTime() % 20 != 0) {
                return;
            }
            var craftingComponent = network.getComponent(AutocraftingNetworkComponent.class);
            boolean shouldEmit = checkShouldEmit(craftingComponent);
            this.shouldEmmitRedstone.accept(shouldEmit);
        }

        private boolean checkShouldEmit(AutocraftingNetworkComponent craftingComponent) {
            for (int i = 0; i < this.filter.getFilterContainer().size(); i++) {
                try {
                    var resource = this.filter.getFilterContainer().get(i);
                    if (resource == null) {
                        results[i] = InterfaceTransferResult.EXPORTED;
                        continue;
                    }
                    if (isTaskRunningForResource(craftingComponent, resource)) {
                        return true;
                    }
                } catch (IllegalStateException e) {
                    RSRReforged.LOGGER.error("Failed to load resource: {}", e.getMessage(), e);
                    results[i] = InterfaceTransferResult.RESOURCE_MISSING;
                }
            }
            return false;
        }

        private boolean isTaskRunningForResource(
                AutocraftingNetworkComponent craftingComponent,
                @UnknownNullability ResourceAmount resource) {
            var patterns = craftingComponent.getPatternsByOutput(resource.resource());
            for (var pattern : patterns) {
                var patternProvider = craftingComponent.getProviderByPattern(pattern);
                if (patternProvider == null) continue;
                for (TaskStatus taskStatus : patternProvider.getTaskStatuses()) {
                    if (taskStatus.info().resource().equals(resource.resource())
                            && taskStatus.info().amount() >= resource.amount()) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Nullable
        public InterfaceTransferResult getLastResult(final int slot) {
            if (results == null) {
                return null;
            }
            return results[slot];
        }
    }

    public record CraftingEmitterData(
            ResourceContainerData filterContainerData, List<ExportingIndicator> exportingIndicators) {
        public static final StreamCodec<RegistryFriendlyByteBuf, CraftingEmitterData> STREAM_CODEC = StreamCodec.composite(
                ResourceContainerData.STREAM_CODEC,
                CraftingEmitterData::filterContainerData,
                ByteBufCodecs.collection(
                        ArrayList::new,
                        enumStreamCodec(ExportingIndicator.values())),
                CraftingEmitterData::exportingIndicators,
                CraftingEmitterData::new);
    }
}