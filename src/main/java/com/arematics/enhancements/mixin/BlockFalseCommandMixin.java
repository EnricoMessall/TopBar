package com.arematics.enhancements.mixin;

import click.isreal.topbar.Topbar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class BlockFalseCommandMixin {
    @Shadow
    protected MinecraftClient client;

    @Shadow
    public void playSound(SoundEvent sound, float volume, float pitch){}

    @Inject( method = "sendMessage", at = {@At( "HEAD" )}, cancellable = true )
    public void sendMessage(Text message, boolean overlay, final CallbackInfo ci )
    {
        Topbar.LOGGER.info("Message: " + message.getString());
        if (Topbar.getInstance().isPreventFalseCommands()
                && message.getContent().toString().matches("^(7|t\\/).*")) {
            Topbar.LOGGER.warn("Blocking Message: " + message.getContent().toString());
            playSound(SoundEvents.ENTITY_DONKEY_ANGRY, 1.0F, 1.0F);
            this.client.getMessageHandler().onGameMessage(Text.literal("" + Formatting.LIGHT_PURPLE + Formatting.BOLD +
                    "ME: " + Formatting.YELLOW + Formatting.ITALIC + "Die Nachricht wurde zu deinem Schutz nicht gesendet, \nda du vermutlich einen Command mit "
                    + Formatting.RED + Formatting.BOLD + Formatting.ITALIC + "/"
                    + Formatting.YELLOW + Formatting.ITALIC + " verschicken wolltest."), false);
            ci.cancel();
        }
    }
}
