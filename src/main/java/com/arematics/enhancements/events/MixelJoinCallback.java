package com.arematics.enhancements.events;

import com.arematics.enhancements.client.Client;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class MixelJoinCallback implements ClientPlayConnectionEvents.Init {

    @Override
    public void onPlayInit(ClientPlayNetworkHandler handler, MinecraftClient client) {
        boolean isMixel = (handler.getConnection() != null &&
                handler.getConnection().getAddress().toString().matches(".*45\\.135\\.203\\.18.*"));
        Client.getInstance().setIsMuxel(isMixel);
    }
}
