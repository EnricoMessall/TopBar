package click.isreal.topbar.mixin;

import click.isreal.topbar.Topbar;
import click.isreal.topbar.client.TopbarClient;
import click.isreal.topbar.domain.BackpackScanData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientNetworkMixin{
    @Shadow
    protected MinecraftClient client;

    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"), cancellable = true)
    private void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        String tn = Thread.currentThread().getName();
        Topbar.LOGGER.info("Thread: " + tn);
        if(tn.startsWith("Netty Client IO")) {
            Topbar.LOGGER.info("Item Pickup animation");
            if (packet.getCollectorEntityId() == client.player.getId()) {
                Topbar.LOGGER.info("Same id");
                Entity collected = client.world.getEntityById(packet.getEntityId());
                if (collected instanceof ItemEntity ie) {
                    Item copy = ie.getStack().copy().getItem();
                    BackpackScanData data = TopbarClient.getInstance().scanData;
                    if(data != null){
                        boolean match = data.capacityPatching().stream().anyMatch(i ->
                                i.isOf(copy));
                        Topbar.LOGGER.info("Match: " + match);
                        if(match){
                            data.addCurrentCapacity(packet.getStackAmount());
                        }
                    }
                }
            }
        }
    }
}
