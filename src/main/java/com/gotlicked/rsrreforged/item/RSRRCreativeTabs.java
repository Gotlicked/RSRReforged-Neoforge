package com.gotlicked.rsrreforged.item;

import com.gotlicked.rsrreforged.RSRReforged;
import com.gotlicked.rsrreforged.block.RSRRBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RSRRCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RSRReforged.MOD_ID);

    public static final Supplier<CreativeModeTab> RSRREFORGED_TAB = CREATIVE_MODE_TABS.register("rsrreforged_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.rsrreforged.rsrreforgedtab"))
            .icon(() -> new ItemStack(RSRRBlocks.REQUESTER.get()))
            .displayItems((_, output) -> {
                output.accept(RSRRBlocks.REQUESTER.get());
                output.accept(RSRRBlocks.CRAFTING_EMITTER.get());
            })
            .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
