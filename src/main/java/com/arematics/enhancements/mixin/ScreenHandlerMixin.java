package com.arematics.enhancements.mixin;

import com.arematics.enhancements.client.Client;
import com.arematics.enhancements.domain.SimpleInventoryInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Timer;
import java.util.TimerTask;

@Mixin(HandledScreens.class)
public class ScreenHandlerMixin {

    @Inject(at = @At("TAIL"), method = "open")
    private static <T extends ScreenHandler> void open(ScreenHandlerType<T> type,
                                                       MinecraftClient client,
                                                       int id,
                                                       Text title,
                                                       CallbackInfo ci) {
        if (type == ScreenHandlerType.GENERIC_9X1 || type == ScreenHandlerType.GENERIC_9X2 ||
                type == ScreenHandlerType.GENERIC_9X3 || type == ScreenHandlerType.GENERIC_9X4 ||
                type == ScreenHandlerType.GENERIC_9X5 || type == ScreenHandlerType.GENERIC_9X6) {
            ScreenHandler handler = client.player.currentScreenHandler;
            if (id == handler.syncId && handler instanceof GenericContainerScreenHandler genScreenHandler) {
                SimpleInventory inv = (SimpleInventory) genScreenHandler.getInventory();
                genScreenHandler.updateToClient();
                SimpleInventoryInfo info = new SimpleInventoryInfo(title.getString(), inv);
                info.setScreenHandler(genScreenHandler);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Client.getInstance().inventories().isOpen(info);
                    }
                }, 500);
            }
        }
    }
}
