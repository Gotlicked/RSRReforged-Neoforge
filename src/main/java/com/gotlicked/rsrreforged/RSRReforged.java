package com.gotlicked.rsrreforged;

import com.gotlicked.rsrreforged.block.CraftingEmitterBlock;
import com.gotlicked.rsrreforged.block.RequesterBlock;
import com.gotlicked.rsrreforged.block.entity.CraftingEmitterBlockEntity;
import com.gotlicked.rsrreforged.block.entity.RequesterBlockEntity;
import com.gotlicked.rsrreforged.container.CraftingEmitterContainer;
import com.gotlicked.rsrreforged.container.RequesterContainer;
import com.mojang.logging.LogUtils;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.AbstractFilterScreen;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(RSRReforged.MOD_ID)
public class RSRReforged {
    public static final String MOD_ID = "rsrreforged";
    public static final String REQUESTER = "requester";
    public static final String CRAFTING_EMITTER = "crafting_emitter";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RSRReforged(IEventBus modEventBus) {
        modEventBus.addListener(this::registerCapabilitiesEvent);
        modEventBus.addListener(this::commonSetup);
        RSRRBlocks.register(modEventBus);
        RSRRCreativeTabs.register(modEventBus);
        RSRRItems.register(modEventBus);
        RSRRBlockEntities.register(modEventBus);
        RSRRMenus.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(
                RequesterBlockEntity.REQUESTER_DESTINATION).add(
                Items.INSTANCE.getStackUpgrade(), 4);

        LOGGER.info("FMLCommonSetupEvent");
    }

    public void registerCapabilitiesEvent(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                (_, _, _, blockEntity, _) -> {
                    if (blockEntity instanceof RequesterBlockEntity requesterBlockEntity) {
                        return requesterBlockEntity.getContainerProvider();
                    }
                    return null;
                }, RSRRBlocks.REQUESTER.get());
        event.registerBlock(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                (_, _, _, blockEntity, _) -> {
                    if (blockEntity instanceof CraftingEmitterBlockEntity craftingEmitterBlockEntity) {
                        return craftingEmitterBlockEntity.getContainerProvider();
                    }
                    return null;
                }, RSRRBlocks.CRAFTING_EMITTER.get());

        LOGGER.info("RegisterCapabilitiesEvent");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("ServerStartingEvent");
    }

    public static class RSRRMenus {
        public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(
                Registries.MENU,
                MOD_ID);
        public static final Supplier<MenuType<RequesterContainer>> REQUESTER_MENU = MENUS.register(
                "requester_menu",
                () -> IMenuTypeExtension.create(
                        (i, inventory,
                         registryFriendlyByteBuf)
                                -> new RequesterContainer(
                                i, inventory,
                                RequesterBlockEntity.RequesterData.STREAM_CODEC.decode(
                                        registryFriendlyByteBuf))));
        public static final Supplier<MenuType<CraftingEmitterContainer>> CRAFTING_EMITTER_MENU = MENUS.register(
                "crafting_emitter_menu",
                () -> IMenuTypeExtension.create(
                        (i, inventory, registryFriendlyByteBuf)
                                -> new CraftingEmitterContainer(
                                i, inventory,
                                CraftingEmitterBlockEntity.CraftingEmitterData.STREAM_CODEC.decode(
                                        registryFriendlyByteBuf))));

        private RSRRMenus() {
        }

        public static void register(IEventBus eventBus) {
            MENUS.register(eventBus);
        }
    }

    public static class RSRRItems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
        public static final Supplier<BlockItem> REQUESTER_ITEM;
        public static final Supplier<BlockItem> CRAFTING_EMITTER_ITEM;

        static {
            REQUESTER_ITEM = ITEMS.registerSimpleBlockItem(
                    REQUESTER,
                    RSRRBlocks.REQUESTER,
                    props
                            -> props);
        }

        static {
            CRAFTING_EMITTER_ITEM = ITEMS.registerSimpleBlockItem(
                    CRAFTING_EMITTER,
                    RSRRBlocks.CRAFTING_EMITTER,
                    props
                            -> props);
        }

        private RSRRItems() {
        }

        public static void register(IEventBus eventBus) {
            ITEMS.register(eventBus);
        }
    }

    public static class RSRRCreativeTabs {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
                Registries.CREATIVE_MODE_TAB, MOD_ID);
        public static final Supplier<CreativeModeTab> RSRREFORGED_TAB;

        static {
            RSRREFORGED_TAB = CREATIVE_MODE_TABS.register(
                    "rsrreforged_tab", () -> CreativeModeTab.builder().title(
                            Component.translatable("creativetab.rsrreforged.rsrreforgedtab")).icon(
                            () -> new ItemStack(
                                    RSRRBlocks.REQUESTER.get())).displayItems(
                            (
                                    _,
                                    output) -> {
                                output.accept(RSRRBlocks.REQUESTER.get());
                                output.accept(RSRRBlocks.CRAFTING_EMITTER.get());
                            }).build());
        }

        private RSRRCreativeTabs() {
        }

        public static void register(IEventBus eventBus) {
            CREATIVE_MODE_TABS.register(eventBus);
        }
    }

    public static class RSRRBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
                BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);


        private RSRRBlockEntities() {
        }

        public static void register(IEventBus eventBus) {
            BLOCK_ENTITY_TYPES.register(eventBus);
        }

        public static final Supplier<BlockEntityType<RequesterBlockEntity>> REQUESTER_BE_TYPE = BLOCK_ENTITY_TYPES.register(
                REQUESTER,
                () -> new BlockEntityType<>(
                        ((RequesterBlock) RSRRBlocks.REQUESTER.get()).getTypedFactory(),
                        RSRRBlocks.REQUESTER.get()));

        public static final Supplier<BlockEntityType<CraftingEmitterBlockEntity>> CRAFTING_EMITTER_BE_TYPE
                = BLOCK_ENTITY_TYPES.register(
                CRAFTING_EMITTER,
                () -> new BlockEntityType<>(
                        ((CraftingEmitterBlock) RSRRBlocks.CRAFTING_EMITTER.get()).getTypedFactory(),
                        RSRRBlocks.CRAFTING_EMITTER.get()));
    }

    public static class RSRRBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MOD_ID);
        public static final Supplier<Block> REQUESTER = BLOCKS.register(
                RSRReforged.REQUESTER,
                RequesterBlock::new);
        public static final Supplier<Block> CRAFTING_EMITTER = BLOCKS.register(
                RSRReforged.CRAFTING_EMITTER,
                CraftingEmitterBlock::new);

        private RSRRBlocks() {
        }

        public static void register(IEventBus eventBus) {
            BLOCKS.register(eventBus);
        }
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class RSRReforgedClient {
        private RSRReforgedClient() {
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(RSRRMenus.REQUESTER_MENU.get(), RequesterScreen::new);
            event.register(RSRRMenus.CRAFTING_EMITTER_MENU.get(), CraftingEmitterScreen::new);
            LOGGER.info("RegisterMenuScreensEvent");
        }

        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("FMLClientSetupEvent");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        public static class CraftingEmitterScreen extends AbstractFilterScreen<CraftingEmitterContainer> {
            protected CraftingEmitterScreen(final CraftingEmitterContainer menu, final Inventory playerInventory,
                                            final Component title) {
                super(menu, playerInventory, title, false);
            }

            @Override
            protected void extractTooltip(final @NonNull GuiGraphicsExtractor graphics, final int x, final int y) {
                if (renderExportingIndicators(
                        font, graphics, leftPos, topPos, x, y, getMenu().getIndicators(),
                        getMenu()::getIndicator)) {
                    return;
                }
                super.extractTooltip(graphics, x, y);
            }
        }

        public static class RequesterScreen extends AbstractFilterScreen<RequesterContainer> {

            protected RequesterScreen(final RequesterContainer menu, final Inventory playerInventory, final Component title) {
                super(menu, playerInventory, title, true);
            }

            @Override
            protected void extractTooltip(final @NonNull GuiGraphicsExtractor graphics, final int x, final int y) {
                if (renderExportingIndicators(
                        font, graphics, leftPos, topPos, x, y, getMenu().getIndicators(),
                        getMenu()::getIndicator)) {
                    return;
                }
                super.extractTooltip(graphics, x, y);
            }
        }
    }
}
