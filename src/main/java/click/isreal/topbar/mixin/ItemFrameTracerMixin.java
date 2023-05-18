package click.isreal.topbar.mixin;

import click.isreal.topbar.Topbar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class ItemFrameTracerMixin extends DrawableHelper {

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
    private final Item air = Registry.ITEM.get(new Identifier("minecraft", "air"));

    @Inject(method = {"render"}, at = {@At("TAIL")})
    public void render(MatrixStack matrices, float tickDelta, final CallbackInfo ci){
        if(Topbar.getInstance().showLookingAt()){
            Entity result = this.client.targetedEntity;
            if(result != null){
                if(result instanceof ItemFrameEntity ife) {
                    ItemStack item = ife.getHeldItemStack();
                    if(item != null) {
                        String text = readId(item);
                        if(text != null){
                            renderItemFrameLookingAt(ife, text, null, matrices);
                        }
                    }
                }
            }
        }
    }

    private void renderItemFrameLookingAt(ItemFrameEntity entity, String text, String secondLine, MatrixStack matrices) {
        if (entity != null) {
            getTextRenderer().draw(matrices, text, 30.0F, 40, 0xfff0f0f0);
            if(secondLine != null){
                getTextRenderer().draw(matrices, text, 30.0F, 50, 0xfff0f0f0);
            }
            itemRenderer.renderInGui(entity.getHeldItemStack(), 7, 36);
        }
    }


    private static String readId(ItemStack item) {
        NbtCompound compound = item.getNbt();
        if(compound != null){
            NbtCompound next = compound.getCompound("display");
            if(next != null){
                NbtList list = next.getList("Lore", NbtString.STRING_TYPE);
                if(list != null && list.size() > 0){
                    String string = list.getString(0);
                    Text serialized = Text.Serializer.fromJson(string);
                    if(serialized != null){
                        if(serialized.getString().contains("ID:")){
                            return serialized.getString();
                        }
                    }
                }
            }
        }
        return null;
    }
}
