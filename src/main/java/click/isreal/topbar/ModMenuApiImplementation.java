package click.isreal.topbar;

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

import com.arematics.enhancements.client.Client;
import com.arematics.enhancements.domain.DiscordMode;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModMenuApiImplementation implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return this::createConfigScreen;
    }

    public Screen createConfigScreen(Screen parent )
    {
        if ( FabricLoader.getInstance().isModLoaded("cloth-config2") )
        {

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal(Formatting.GOLD + "Muxel Enhancements"));
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("Muxel-Enhancements"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.setBackground(Identifier.tryParse("minecraft:textures/block/dragon_egg.png"));
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Topbar"), Topbar.getInstance().isTopbar()).setDefaultValue(false).setTooltip(Text.literal("If enabled, scoreboard moves to the top of your screen")).setSaveConsumer(Topbar.getInstance()::setTopbar).build());
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
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Tool break warning"), Topbar.getInstance().isBreakwarnEnabled()).setDefaultValue(false).setTooltip(Text.literal("If enabled, a warning is displayed if the tool being used is about to be destroyed.")).setSaveConsumer(Topbar.getInstance()::setBreakwarnEnabled).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Unsecure Server Warning"), Topbar.getInstance().unsecureServerWarning()).setDefaultValue(false).setTooltip(Text.literal("If disabled, no Chat couldn't be verified message is displayed")).setSaveConsumer(Topbar.getInstance()::setUnsecureServerWarning).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Horn Audio"), Topbar.getInstance().hornAudio()).setDefaultValue(false).setTooltip(Text.literal("If disabled, horn sounds are blocked for your client")).setSaveConsumer(Topbar.getInstance()::setHornAudio).build());
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Looking At"), Topbar.getInstance().showLookingAt()).setDefaultValue(true).setTooltip(Text.literal("Get Information's about the next solid block you are looking at")).setSaveConsumer(Topbar.getInstance()::setLookingAt).build());
            builder.setSavingRunnable(() -> Topbar.getInstance().saveConfig());
            return builder.build();
        }
        return null;
    }
}
