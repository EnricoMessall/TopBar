package click.isreal.topbar.mixin;

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
import click.isreal.topbar.client.TopbarClient;
import click.isreal.topbar.domain.BackpackScanData;
import click.isreal.topbar.domain.MixelWorld;
import click.isreal.topbar.domain.UserData;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


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
    private String currentFacingInfo;
    private final Item air = Registry.ITEM.get(new Identifier("minecraft", "air"));

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffectOverlay(MatrixStack matrices, final CallbackInfo ci) {
        if (MixelWorld.inWorld() && Topbar.getInstance().isTopbar()) {
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
                        if (TopbarClient.getInstance().isMixelPixel()) {
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
                            RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
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

        if (MixelWorld.inWorld() && Topbar.getInstance().isTopbar()) {
            int offsetLeft = 2;
            int offsetRight = 2;
            String fps = TopbarClient.getInstance().getFPS();
            String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

            fill(matrices, 0, 0, this.scaledWidth, 10, Topbar.getInstance().getColorBackground());
            this.getTextRenderer().getClass();

            if (Topbar.getInstance().isFpsShow()) {
                offsetLeft += this.getTextRenderer().getWidth(Formatting.strip(fps + TopbarClient.getInstance().strSplitter));
            }

            if (Topbar.getInstance().isTimeShow()) {
                offsetRight += this.getTextRenderer().getWidth(" | 00:00:00");
            }

            this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strTopLeft, offsetLeft, 1, 0xfff0f0f0);
            this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strTopRight, this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(TopbarClient.getInstance().strTopRight)) - offsetRight, 1, 0xfff0f0f0);
            if (Topbar.getInstance().isFpsShow()) {
                this.getTextRenderer().draw(matrices, fps + TopbarClient.getInstance().strSplitter, 2, 1, Topbar.getInstance().getFpsColor());
            }
            if (Topbar.getInstance().isTimeShow()) {
                this.getTextRenderer().draw(matrices, TopbarClient.getInstance().strSplitter + Formatting.RESET + time, this.scaledWidth - offsetRight, 1, Topbar.getInstance().getTimeColor());
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
    public void render(MatrixStack matrices, float tickDelta, final CallbackInfo ci){
        BackpackScanData data = TopbarClient.getInstance().scanData;
        if(data != null) {
            String text = data.currentCapacity() + "/" + data.maxCapacity();
            this.getTextRenderer().draw(matrices, data.backpackName(),
                    this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(data.backpackName())) - 2,
                    this.scaledHeight - 50, 0xfff0f0f0);
            this.getTextRenderer().draw(matrices, text,
                            this.scaledWidth - this.getTextRenderer().getWidth(Formatting.strip(text)) - 2,
                            this.scaledHeight - 40, 0xfff0f0f0);
        }
        if(Topbar.getInstance().showLookingAt()){
            HitResult result = client.player.raycast(20.0, 0.0F, false);
            if(result != null && result.getType().equals(HitResult.Type.BLOCK)){
                BlockPos blockPos = ((BlockHitResult)result).getBlockPos();
                BlockState state = this.client.world.getBlockState(blockPos);
                BlockEntity entity = client.world.getBlockEntity(blockPos);
                if(state != null && !state.getBlock().asItem().getDefaultStack().copy().isOf(air)) {
                    this.currentFacing = state.getBlock().asItem().getDefaultStack().copy();
                    if(entity instanceof BeehiveBlockEntity bbe) {
                        Topbar.LOGGER.info(bbe.getBees().toString());
                        this.currentFacingInfo = "Bienen: " + bbe.getBees().size();
                    }else this.currentFacingInfo = "";
                }else {
                    this.currentFacing = null;
                    this.currentFacingInfo = "";
                }
            }else if(result != null && result.getType().equals(HitResult.Type.ENTITY)){
                Topbar.LOGGER.info("Is entity");
            }
            if(this.currentFacing != null) {
                renderLookingAt(matrices);
            }
        }
    }

    private void renderLookingAt(MatrixStack matrices) {
        if (this.currentFacing != null) {
            getTextRenderer().draw(matrices, currentFacing.getName(), 30.0F, 20, 0xfff0f0f0);
            if(this.currentFacingInfo != null){
                Text info = Text.literal(this.currentFacingInfo);
                List<OrderedText> list = client.textRenderer.wrapLines(info, 100);
                if (list.size() == 1) {
                    getTextRenderer().draw(matrices, list.get(0), 30.0F, 32, 0xfff0f0f0);
                } else {
                    int var10000 = 32 / 2;
                    int var10001 = list.size();
                    int l = var10000 - var10001 * 9 / 2;

                    for(Iterator var12 = list.iterator(); var12.hasNext(); l += 9) {
                        OrderedText orderedText = (OrderedText)var12.next();
                        this.getTextRenderer().draw(matrices, orderedText, 30.0F, (float)l, 0xfff0f0f0);
                    }
                }
            }

            itemRenderer.renderInGui(this.currentFacing, 7, 16);
        }
    }
}




