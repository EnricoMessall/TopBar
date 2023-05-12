package click.isreal.topbar.client;

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
import click.isreal.topbar.domain.*;
import click.isreal.topbar.events.InventoryOpenEvent;
import click.isreal.topbar.events.JobBackpackOpenerCallback;
import click.isreal.topbar.events.MixelJoinCallback;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment( EnvType.CLIENT )
public class TopbarClient implements ClientModInitializer
{

    private static TopbarClient instance;
    private final MinecraftClient client = MinecraftClient.getInstance();
    // Storage for all TopBar Strings (Parts), so we only need to build/change them,
    // when things really changed. Saves some time in gui-render thread
    public final String strSplitter = Formatting.GRAY + " | ";
    public String strTopLeft = "";
    public String strTopRight = "";
    public DiscordRPC dc;
    private List<ScoreboardMapping> scoreboardMappings = new ArrayList<>();
    private boolean _isMixel = false;
    private final InventoryOpenEvent openEvent = new InventoryOpenEvent();
    public BackpackScanData scanData = null;

    {
        instance = this;
    }

    public static TopbarClient getInstance()
    {
        return instance;
    }

    public MixelWorld getWorld(){
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
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> setisMixel(false));
        ClientPlayConnectionEvents.INIT.register(new MixelJoinCallback());
        inventories().register(new JobBackpackOpenerCallback());
        for(ScoreboardMapping mapping : ScoreboardMapping.values()){
            this.scoreboardMappings.add(mapping);
            Topbar.LOGGER.info("Enabled scoreboard mapping: " + mapping.name());
        }
    }

    public void setisMixel( boolean Status )
    {
        Topbar.LOGGER.info((Status ? "Joining" : "Leaving") + " Mixelpixel Server");
        _isMixel = Status;
    }

    public boolean isMixelPixel()
    {
        return _isMixel;
    }

    public String buildName(MixelWorld world){
        return buildName(world, true);
    }

    public String buildName(MixelWorld world, boolean formatting){
        StringBuilder builder = new StringBuilder();
        if(formatting) builder.append(world.getFormatting().toString() + Formatting.BOLD);
        builder.append(world.getType().getName().toUpperCase());
        if(world.getType() == MixelWorldType.FARMWORLD || world.getType() == MixelWorldType.SPAWN)
            builder.append("-").append(world.getSubtype());
        if(world.getType() == MixelWorldType.SMALL_CB || world.getType() == MixelWorldType.BIG_CB)
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
            MixelWorld world = getWorld();
            switch (world) {
                case HUB, SPAWN_1, SPAWN_2, SPAWN_3, SPAWN_4 -> {
                    strTopLeft = buildName(world) + Formatting.GRAY + " - " + UserData.current().rank();
                    strTopRight = "";
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
                    if (null != MinecraftClient.getInstance().world) {
                        if (World.END == MinecraftClient.getInstance().world.getRegistryKey())
                            UserData.current().setDimension("End");
                        else if (World.NETHER == MinecraftClient.getInstance().world.getRegistryKey())
                            UserData.current().setDimension("Nether");
                        else if (World.OVERWORLD == MinecraftClient.getInstance().world.getRegistryKey())
                            UserData.current().setDimension("Overworld");
                        else UserData.current().setDimension("");
                    } else UserData.current().setDimension("");
                    if (Topbar.getInstance().isStreamerMode()) strTopRight = Formatting.YELLOW + "[STREAMING]";
                    else strTopRight = UserData.current().money();
                }
                case SMALL_AQUA, SMALL_DONNER, SMALL_FLORA, SMALL_VULKAN, BIG_AQUA, BIG_DONNER, BIG_FLORA, BIG_VULKAN -> {
                    strTopLeft += buildName(world);
                    UserData.current().setDimension("");
                    if (Topbar.getInstance().isStreamerMode()) strTopRight = Formatting.YELLOW + "[STREAMING]";
                    else strTopRight = UserData.current().money();
                }
                default -> strTopRight = Formatting.RED + "?"; // at this moment we don't know what to do ;-)
            }
        }
    }

    public Screen createConfigScreen( Screen parent )
    {
        if ( FabricLoader.getInstance().isModLoaded("cloth-config2") )
        {

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal(Formatting.GOLD + "Muxel Enhancements"));
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("Muxel-Enhancements"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.setBackground(Identifier.tryParse("minecraft:textures/block/dragon_egg.png"));
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Topbar"), Topbar.getInstance().isTopbar()).setDefaultValue(true).setTooltip(Text.literal("If enabled, scoreboard moves to the top of your screen")).setSaveConsumer(Topbar.getInstance()::setTopbar).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Streamer-Mode"), Topbar.getInstance().isStreamerMode()).setDefaultValue(false).setTooltip(Text.literal("If enabled, your ingame money value would be hidden on topbar only. Not disabled on scoreboard. Horn Audio will be force disabled and Minimal Discord will be force enabled")).setSaveConsumer(Topbar.getInstance()::setStreamerMode).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Background"), Topbar.getInstance().getColorBackground()).setDefaultValue(0xf0000000).setAlphaMode(true).setTooltip(Text.literal("Background color of the topbar in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setColorBackground).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show FPS"), Topbar.getInstance().isFpsShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, FPS is shown left-most on the topbar.")).setSaveConsumer(Topbar.getInstance()::setFpsShow).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color FPS"), Topbar.getInstance().getFpsColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of FPS in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setFpsColor).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Time"), Topbar.getInstance().isTimeShow()).setDefaultValue(true).setTooltip(Text.literal("If enabled, Time of your computer is shown right-most on the topbar.")).setSaveConsumer(Topbar.getInstance()::setTimeShow).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Time"), Topbar.getInstance().getTimeColor()).setDefaultValue(0xff808080).setAlphaMode(true).setTooltip(Text.literal("Sets the textcolor of Time in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setTimeColor).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Prevent sending false commands"), Topbar.getInstance().isPreventFalseCommands()).setDefaultValue(true).setTooltip(Text.literal("Prevents sending Chat-Messages starting with '7' or 't/'. As this are the most common typo errors.")).setSaveConsumer(Topbar.getInstance()::setPreventFalseCommands).build());
            general.addEntry(entryBuilder.startColorField(Text.literal("Color Loading Screen"), Topbar.getInstance().getLoadscreenColor()).setDefaultValue(0xffff007d).setAlphaMode(true).setTooltip(Text.literal("Sets the background color of the loadingscreen(the one with the mojang logo) in Hex. (#AARRGGBB)")).setSaveConsumer(Topbar.getInstance()::setLoadscreenColor).build());
            general.addEntry(entryBuilder.startEnumSelector(
                    Text.literal("Discord Mode"), DiscordMode.class, Topbar.getInstance().getDiscordMode())
                    .setDefaultValue(DiscordMode.FULL)
                    .setTooltip(Text.translatable("text.muxel-enhancements.settings.discord"))
                    .setSaveConsumer(Topbar.getInstance()::setDiscordMode)
                    .build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Tool break warning"), Topbar.getInstance().isBreakwarnEnabled()).setDefaultValue(true).setTooltip(Text.literal("If enabled, a warning is displayed if the tool being used is about to be destroyed.")).setSaveConsumer(Topbar.getInstance()::setBreakwarnEnabled).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Toggle Unsecure Server Warning"), Topbar.getInstance().unsecureServerWarning()).setDefaultValue(false).setTooltip(Text.literal("If disabled, no Chat couldn't be verified message is displayed")).setSaveConsumer(Topbar.getInstance()::setUnsecureServerWarning).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Toggle Horn Audio"), Topbar.getInstance().hornAudio()).setDefaultValue(false).setTooltip(Text.literal("If disabled, horn sounds are blocked for your client")).setSaveConsumer(Topbar.getInstance()::setHornAudio).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Looking At"), Topbar.getInstance().showLookingAt()).setDefaultValue(true).setTooltip(Text.literal("Get Information's about the next solid block you are looking at")).setSaveConsumer(Topbar.getInstance()::setLookingAt).build());
            builder.setSavingRunnable(() -> Topbar.getInstance().saveConfig());
            return builder.build();
        }
        return null;
    }
}
