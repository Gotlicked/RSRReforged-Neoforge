package com.gotlicked.rsrreforged;

import com.gotlicked.rsrreforged.client.screen.CraftingEmitterScreen;
import com.gotlicked.rsrreforged.client.screen.RequesterScreen;
import com.gotlicked.rsrreforged.menu.RSRRMenus;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = RSRReforged.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RSRReforged.MOD_ID, value = Dist.CLIENT)
public class RSRReforgedClient {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(RSRRMenus.REQUESTER_MENU.get(), RequesterScreen::new);
        event.register(RSRRMenus.CRAFTING_EMITTER_MENU.get(), CraftingEmitterScreen::new);
        RSRReforged.LOGGER.info("RegisterMenuScreensEvent");
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        RSRReforged.LOGGER.info("FMLClientSetupEvent");
        RSRReforged.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
