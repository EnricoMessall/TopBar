package com.arematics.enhancements.mixin;

/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2022 YveIce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

import click.isreal.topbar.Topbar;
import com.arematics.enhancements.client.Client;
import com.arematics.enhancements.domain.ImprovedClientWorld;
import com.arematics.enhancements.domain.ImprovedSign;
import com.arematics.enhancements.domain.MuxelWorld;
import com.arematics.enhancements.domain.UserData;
import com.arematics.enhancements.domain.shops.Shop;
import com.arematics.enhancements.domain.shops.ShopDataCache;
import com.arematics.enhancements.domain.shops.ShopItem;
import com.arematics.enhancements.util.ItemHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.text.html.Option;
import java.util.*;


@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow
    private int scaledWidth;

    @Shadow
    private ItemRenderer itemRenderer;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Nullable
    private ItemStack currentFacing;
    @Nullable
    private ItemFrameEntity currentItemFrame;

    private final ShopDataCache cache = ShopDataCache.getInstance();

    @Nullable
    private String currentFacingInfo;
    private final Item air = Registries.ITEM.get(new Identifier("minecraft", "air"));
    private final Item itemFrame = Registries.ITEM.get(new Identifier("minecraft", "item_frame"));

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffectOverlay(MatrixStack matrices, final CallbackInfo ci) {
        if (MuxelWorld.inWorld() && Topbar.getInstance().isTopbar()) {
            Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
            if (!collection.isEmpty()) {
                RenderSystem.enableBlend();
                int i = 0;
                int j = 0;
                StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
                List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
                RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);

                for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                    StatusEffect statusEffect = statusEffectInstance.getEffectType();
                    if (statusEffectInstance.shouldShowIcon()) {
                        int k = this.scaledWidth;
                        int l = 1;
                        if (this.client.isDemo()) {
                            l += 15;
                        }
                        if (Client.getInstance().isMuxelPixel()) {
                            l += 10;
                        }

                        if (statusEffect.isBeneficial()) {
                            ++i;
                            k -= 25 * i;
                        } else {
                            ++j;
                            k -= 25 * j;
                            l += 26;
                        }

                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        float f;
                        if (statusEffectInstance.isAmbient()) {
                            f = 1.0F;
                            this.drawTexture(matrices, k, l, 165, 166, 24, 24);
                        } else {
                            this.drawTexture(matrices, k, l, 141, 166, 24, 24);
                            if (statusEffectInstance.getDuration() <= 200) {
                                int m = 10 - statusEffectInstance.getDuration() / 20;
                                f = MathHelper.clamp((float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F) * MathHelper.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
                            } else {
                                f = 1.0F;
                            }
                        }

                        Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
                        int finalK = k;
                        int finalL = l;
                        list.add(() -> {
                            RenderSystem.setShaderTexture(0, sprite.getAtlasId());
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f);
                            drawSprite(matrices, finalK + 3, finalL + 3, this.getZOffset(), 18, 18, sprite);
                        });
                    }
                }

                list.forEach(Runnable::run);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (ci.isCancellable()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = {"renderScoreboardSidebar"}, at = {@At("HEAD")}, cancellable = true)
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, final CallbackInfo callbackInfo) {

        if (MuxelWorld.inWorld() && Topbar.getInstance().isTopbar()) {
            int offsetLeft = 2;
            int offsetRight = 2;
            String fps = Client.getInstance().getFPS();
            String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

            fill(matrices, 0, 0, this.scaledWidth, 10, Topbar.getInstance().getColorBackground());
            this.getTextRenderer().getClass();

            if (Topbar.getInstance().isFpsShow()) {
                offsetLeft += this.getTextRenderer().getWidth(Formatting.strip(fps + Client.getInstance().strSplitter));
            }

            if (Topbar.getInstance().isTimeShow()) {
                offsetRight += this.getTextRenderer().getWidth(" | 00:00:00");
            }

            this.getTextRenderer().draw(matrices, Client.getInstance().strTopLeft, offsetLeft, 1, 0xfff0f0f0);
            this.getTextRenderer().draw(matrices, Client.getInstance().strTopRight, this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(Client.getInstance().strTopRight)) - offsetRight, 1, 0xfff0f0f0);
            if (Topbar.getInstance().isFpsShow()) {
                this.getTextRenderer().draw(matrices, fps + Client.getInstance().strSplitter, 2, 1, Topbar.getInstance().getFpsColor());
            }
            if (Topbar.getInstance().isTimeShow()) {
                this.getTextRenderer().draw(matrices, Client.getInstance().strSplitter + Formatting.RESET + time, this.scaledWidth - offsetRight, 1, Topbar.getInstance().getTimeColor());
            }

            this.getTextRenderer().draw(matrices, UserData.current().cbPlotName(),
                    this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(UserData.current().cbPlotName())) - 2,
                    this.scaledHeight - 19, 0xfff0f0f0);

            this.getTextRenderer().draw(matrices, UserData.current().cbPlotOwner(),
                    this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(UserData.current().cbPlotOwner())) - 2,
                    this.scaledHeight - 10, 0xfff0f0f0);
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"render"}, at = {@At("TAIL")})
    public void render(MatrixStack matrices, float tickDelta, final CallbackInfo ci) {
        extractShopData();
        if (Topbar.getInstance().showLookingAt()) {
            HitResult result = Client.getInstance().watchingAt();
            ItemFrameEntity entity = Client.getInstance().target(ItemFrameEntity.class);
            if (entity != null) {
                this.currentFacing = null;
                this.currentFacingInfo = "";
                this.currentItemFrame = entity;
                ItemStack item = entity.getHeldItemStack();
                ItemHelper.readLore(item).stream()
                        .filter((line) -> line.contains("ID:"))
                        .findFirst().ifPresent(text -> item.setCustomName(Text.literal(text)));
            } else if (result.getType().equals(HitResult.Type.BLOCK)) {
                this.currentItemFrame = null;
                BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
                BlockState state = this.client.world.getBlockState(blockPos);
                BlockEntity entityblock = client.world.getBlockEntity(blockPos);
                if (state != null && !state.getBlock().asItem().getDefaultStack().copy().isOf(air)) {
                    this.currentFacing = state.getBlock().asItem().getDefaultStack().copy();
                    if (entityblock instanceof BeehiveBlockEntity bbe) {
                        this.currentFacingInfo = "Bienen: " + bbe.getBees().size();
                    } else this.currentFacingInfo = "";
                } else {
                    this.currentFacing = null;
                    this.currentFacingInfo = "";
                }
            }
            if (this.currentFacing != null) {
                renderLookingAt(matrices);
            }
            if(this.currentItemFrame != null){
                renderCurrentItemFrame(this.currentItemFrame, matrices);
            }
        }
    }

    private void renderLookingAt(MatrixStack matrices) {
        renderItemStack(this.currentFacing, matrices, this.currentFacingInfo);
    }

    private void renderItemStack(ItemStack itemStack, MatrixStack matrices, String... lines){
        if(itemStack != null && !itemStack.isOf(air)){
            getTextRenderer().draw(matrices, itemStack.getName().getString(), 30.0F, 20, 0xfff0f0f0);
            int begin = 32;
            for(String line: lines){
                if(line != null) {
                    getTextRenderer().draw(matrices, line, 30.0F, begin, 0xfff0f0f0);
                    begin += 12;
                }
            }
            itemRenderer.renderInGui(itemStack, 7, 16);
        }
    }

    private void renderCurrentItemFrame(ItemFrameEntity ife, MatrixStack matrices){
        ItemStack item = ife.getHeldItemStack();
        if (!item.isOf(air)) {
            ImprovedSign sign = Client.getInstance().world().sign(ife.getBlockPos().down());
            if (sign != null) {
                ShopItem shopItem = sign.toMixelShopItem(item);
                if(shopItem != null){
                    if(shopItem.empty()){
                        renderItemFrameLookingAt(ife, matrices, shopItem.displayName(), Formatting.RED + "Ausverkauft");
                    }else{
                        String name = shopItem.bookEnchantment() != null ? shopItem.bookEnchantment() : shopItem.displayName();
                        String kaufen = shopItem.amount() + "x kaufen für " + shopItem.price() + " ⛀";
                        renderItemFrameLookingAt(ife, matrices, name, kaufen);
                    }
                }
            }
        }else{
            renderItemStack(itemFrame.getDefaultStack(), matrices);
        }
    }

    private void renderItemFrameLookingAt(ItemFrameEntity entity, MatrixStack matrices, String... lines) {
        if (entity != null) {
            int begin = 20;
            for(String line: lines){
                getTextRenderer().draw(matrices, line, 30.0F, begin, 0xfff0f0f0);
                begin += 12;
            }
            itemRenderer.renderInGui(entity.getHeldItemStack(), 7, 16);
        }
    }

    private void extractShopData(){
        ImprovedClientWorld world = Client.getInstance().world();
        world.getNearPlayer(ItemFrameEntity.class)
                .forEach(this::addItemToCache);
    }

    private void addItemToCache(ItemFrameEntity ife){
        ItemStack item = ife.getHeldItemStack();
        if(item == null) return;
        ImprovedSign sign = Client.getInstance().world().sign(ife.getBlockPos().down());
        if (sign != null) {
            ShopItem shopItem =  sign.toMixelShopItem(item);
            if(shopItem != null){
                String shopName = sign.getText(1);
                Shop shop = new Shop(shopName, new ArrayList<>());
                cache.addItem(shop, shopItem);
            }
        }
    }
}




