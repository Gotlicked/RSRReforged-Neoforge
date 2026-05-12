package com.gotlicked.rsrreforged.menu;

import com.gotlicked.rsrreforged.RSRReforged;
import com.gotlicked.rsrreforged.common.container.CraftingEmitterContainer;
import com.gotlicked.rsrreforged.common.container.RequesterContainer;
import com.gotlicked.rsrreforged.common.data.CraftingEmitterData;
import com.gotlicked.rsrreforged.common.data.RequesterData;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RSRRMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(
            Registries.MENU,
            RSRReforged.MOD_ID);

    public static final Supplier<MenuType<RequesterContainer>> REQUESTER_MENU = MENUS.register(
            "requester_menu",
            () -> IMenuTypeExtension.create(
                    (i, inventory,
                     registryFriendlyByteBuf)
                            -> new RequesterContainer(
                            i, inventory,
                            RequesterData.STREAM_CODEC.decode(
                                    registryFriendlyByteBuf))));

    public static final Supplier<MenuType<CraftingEmitterContainer>> CRAFTING_EMITTER_MENU = MENUS.register(
            "crafting_emitter_menu",
            () -> IMenuTypeExtension.create(
                    (i, inventory, registryFriendlyByteBuf)
                            -> new CraftingEmitterContainer(
                            i, inventory,
                            CraftingEmitterData.STREAM_CODEC.decode(
                                    registryFriendlyByteBuf))));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
