package com.gotlicked.rsrreforged.client.screen;

import com.gotlicked.rsrreforged.common.container.RequesterContainer;
import com.refinedmods.refinedstorage.common.support.AbstractFilterScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class RequesterScreen extends AbstractFilterScreen<RequesterContainer> {
    public RequesterScreen(final RequesterContainer menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title, true);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void extractTooltip(final @NonNull GuiGraphicsExtractor graphics, final int x, final int y) {
        if (renderExportingIndicators(font, graphics, leftPos, topPos, x, y, getMenu().getIndicators(),
                getMenu()::getIndicator)) {
            return;
        }
        super.extractTooltip(graphics, x, y);
    }

}
