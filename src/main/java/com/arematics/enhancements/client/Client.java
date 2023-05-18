package com.arematics.enhancements.client;

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
import com.arematics.enhancements.domain.*;
import com.arematics.enhancements.events.InventoryOpenEvent;
import com.arematics.enhancements.events.MixelJoinCallback;
import com.arematics.enhancements.scoreboard.mappers.CitybuildMapper;
import com.arematics.enhancements.scoreboard.mappers.KffaMapper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment( EnvType.CLIENT )
public class Client implements ClientModInitializer
{

    @Nullable
    private static ImprovedClientWorld clientWorld;

    private static Client instance;
    private final MinecraftClient client = MinecraftClient.getInstance();
    // Storage for all TopBar Strings (Parts), so we only need to build/change them,
    // when things really changed. Saves some time in gui-render thread
    public final String strSplitter = Formatting.GRAY + " | ";
    public String strTopLeft = "";
    public String strTopRight = "";
    public DiscordRPC dc;
    private List<ScoreboardMapping> scoreboardMappings = new ArrayList<>();
    private boolean _isMuxel = false;
    private final InventoryOpenEvent openEvent = new InventoryOpenEvent();

    {
        instance = this;
    }

    public static Client getInstance()
    {
        return instance;
    }

    public MuxelWorld getWorld(){
        return UserData.current().mixelWorld();
    }

    public List<ScoreboardMapping> scoreboardMappings() {
        return scoreboardMappings;
    }

    public InventoryOpenEvent inventories() {
        return openEvent;
    }

    @Override
    public void onInitializeClient()
    {
        this.initEventCallbacks();
        System.out.println("\\u001b[0;35mMuxel-Enhancements: started at " + java.time.LocalDateTime.now());
        try {
            dc = new DiscordRPC();
        }catch (Exception e) {
            Topbar.LOGGER.warn("Error starting DC: \n" + e.getMessage());
        }
    }

    private void initEventCallbacks(){
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> setIsMuxel(false));
        ClientPlayConnectionEvents.INIT.register(new MixelJoinCallback());
        addScoreboardMapping(new CitybuildMapper());
        addScoreboardMapping(new KffaMapper());
    }

    private void addScoreboardMapping(ScoreboardMapping mapping){
        this.scoreboardMappings.add(mapping);
        Topbar.LOGGER.info("Enabled scoreboard mapping: " + mapping.getClass().getSimpleName());
    }

    public void setIsMuxel(boolean Status )
    {
        Topbar.LOGGER.info((Status ? "Joining" : "Leaving") + " Mixelpixel Server");
        _isMuxel = Status;
    }

    public boolean isMuxelPixel()
    {
        return _isMuxel;
    }

    public String buildName(MuxelWorld world){
        return buildName(world, true);
    }

    public String buildName(MuxelWorld world, boolean formatting){
        StringBuilder builder = new StringBuilder();
        if(formatting) builder.append(world.getFormatting().toString() + Formatting.BOLD);
        builder.append(world.getType().getName().toUpperCase());
        if(world.getType() == MuxelWorldType.FARMWORLD || world.getType() == MuxelWorldType.SPAWN)
            builder.append("-").append(world.getSubtype());
        if(world.getType() == MuxelWorldType.SMALL_CB || world.getType() == MuxelWorldType.BIG_CB)
            builder.append(" ").append(world.getSubtype());
        return builder.toString();
    }

    public String getFPS()
    {
        if ( Topbar.getInstance().isFpsShow() )
        {
            StringBuilder fps = new StringBuilder();
            fps.append(this.client.fpsDebugString.split("fps")[0].trim());
            while ( fps.length() < 3 )
            {
                fps.insert(0, " ");
            }
            fps.append(" FPS");
            return fps.toString();
        }
        return "";
    }

    public void updateTopBar(@Nullable ScoreboardUpdate update)
    {
        if(update != null) {
            this.scoreboardMappings.stream()
                    .filter(m -> m.test(update))
                    .forEach(m -> m.accept(update));
        }
        // reserve 7 char space for fps String, if we show it
        strTopLeft = "" + Formatting.BLUE + Formatting.BOLD + "MixelPixel.net" + Formatting.GRAY + " - ";

        if(UserData.current().getInjection(Winter22Event.class) != null &&
                UserData.current().getInjection(Winter22Event.class).tuer() != null){
            Winter22Event event = UserData.current().getInjection(Winter22Event.class);
            strTopLeft = Formatting.RED + "Winterevent";
            strTopRight = Formatting.YELLOW + "Türchen: " + event.tuer() +
                    Formatting.GRAY + " | " + Formatting.YELLOW + "Modus: " + event.modus();
            if(event.checkpoints() != null)
                strTopRight += Formatting.GRAY + " | " + Formatting.YELLOW + "Checkpoint: " + event.checkpoints();
        }else {
            MuxelWorld world = getWorld();
            switch (world) {
                case HUB -> {
                    strTopLeft = buildName(world) + Formatting.GRAY + " - " + UserData.current().rank();
                    strTopRight = "";
                }
                case SPAWN_1, SPAWN_2, SPAWN_3, SPAWN_4 -> {
                    strTopLeft = buildName(world) + Formatting.GRAY + " - " + UserData.current().rank();
                    String money = Topbar.getInstance().isStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
                    String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
                    strTopRight = jubilaeum + money;
                }
                case KFFA -> {
                    strTopLeft = "" + Formatting.BLUE + Formatting.BOLD + "MP" + Formatting.GRAY + " - "
                            + buildName(world) + Formatting.GRAY + " - " + UserData.current().kffaMap()
                            + UserData.current().kffaMapSwitch() + Formatting.GRAY + " - " + UserData.current().rank();
                    strTopRight = UserData.current().rankPoints() + UserData.current().aufstiegPoints() +
                            strSplitter + UserData.current().kffaKD() + strSplitter;
                    if (Topbar.getInstance().isStreamerMode()) strTopRight += Formatting.YELLOW + "[STREAMING]";
                    else strTopRight += UserData.current().money();
                }
                case FARMWORLD_1, FARMWORLD_2, FARMWORLD_3, FARMWORLD_4 -> {
                    strTopLeft += buildName(world);
                    if (world().isOfType(World.END)) UserData.current().setDimension("End");
                    else if (world().isOfType(World.NETHER)) UserData.current().setDimension("Nether");
                    else if (world().isOfType(World.OVERWORLD)) UserData.current().setDimension("Overworld");
                    else UserData.current().setDimension("");
                    String money = Topbar.getInstance().isStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
                    String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
                    strTopRight = jubilaeum + money;
                }
                case SMALL_AQUA, SMALL_DONNER, SMALL_FLORA, SMALL_VULKAN, BIG_AQUA, BIG_DONNER, BIG_FLORA, BIG_VULKAN -> {
                    strTopLeft += buildName(world);
                    UserData.current().setDimension("");
                    String money = Topbar.getInstance().isStreamerMode() ? Formatting.YELLOW + "[STREAMING]" : UserData.current().money();
                    String jubilaeum = UserData.current().getJubiProgress() != null ? UserData.current().getJubiProgress() + Formatting.GRAY + " | " : "";
                    strTopRight = jubilaeum + money;
                }
                default -> strTopRight = Formatting.RED + "?"; // at this moment we don't know what to do ;-)
            }
        }
    }

    @Nullable
    public ImprovedSignEntity toSign(BlockPos blockPos){
        ImprovedClientWorld world = world();
        if(world.world() == null) return null;
        BlockState state = world.world().getBlockState(blockPos);
        BlockEntity entity = world.world().getBlockEntity(blockPos);
        return null;
    }

    @NotNull
    public ImprovedClientWorld world(){
        ClientWorld world = client.world;
        if(clientWorld == null || clientWorld.world() != world){
            clientWorld = new ImprovedClientWorld(world);
        }
        return clientWorld;
    }

    @NotNull
    public HitResult watchingAt(){
        return watchingAt(false);
    }

    @NotNull
    public HitResult watchingAt(boolean fluids){
        return client.player.raycast(20.0, 0.0F, fluids);
    }

    @Nullable
    public <T extends Entity> T target(Class<T> clazz){
        try{
            return clazz.cast(client.targetedEntity);
        }catch (Exception e){
            return null;
        }
    }
}
