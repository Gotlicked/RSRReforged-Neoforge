package com.gotlicked.rsrreforged;

import com.gotlicked.rsrreforged.block.RSRRBlocks;
import com.gotlicked.rsrreforged.block.entity.CraftingEmitterBlockEntity;
import com.gotlicked.rsrreforged.block.entity.RSRRBlockEntities;
import com.gotlicked.rsrreforged.block.entity.RequesterBlockEntity;
import com.gotlicked.rsrreforged.item.RSRRCreativeTabs;
import com.gotlicked.rsrreforged.item.RSRRItems;
import com.gotlicked.rsrreforged.menu.RSRRMenus;
import com.mojang.logging.LogUtils;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(RSRReforged.MOD_ID)
public class RSRReforged {
    public static final String MOD_ID = "rsrreforged";
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

        RefinedStorageApi.INSTANCE.getUpgradeRegistry().forDestination(RequesterBlockEntity.REQUESTER_DESTINATION).add(Items.INSTANCE.getStackUpgrade(), 4);

        LOGGER.info("FMLCommonSetupEvent");
    }

    public void registerCapabilitiesEvent(RegisterCapabilitiesEvent event) {
        event.registerBlock(RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(), (_, _, _, blockEntity, _) -> {
            if (blockEntity instanceof RequesterBlockEntity requesterBlockEntity) {
                return requesterBlockEntity.getContainerProvider();
            }
            return null;
        }, RSRRBlocks.REQUESTER.get());
        event.registerBlock(RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(), (_, _, _, blockEntity, _) -> {
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
}
