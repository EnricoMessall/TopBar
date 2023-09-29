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
import com.arematics.enhancements.domain.MuxelWorld;
import com.arematics.enhancements.domain.ScoreboardUpdate;
import com.arematics.enhancements.domain.UserData;
import com.arematics.enhancements.domain.Winter22Event;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Environment( EnvType.CLIENT )
@Mixin( Scoreboard.class )
public class ScoreboardMixins
{

    @Shadow
    @Final
    private Map<String, ScoreboardObjective> objectives;

    private String stripStr( String text )
    {
        return text.replaceAll("§[0-9a-fklmnor]", "").trim();
    }

    private void updateObjective( String player, ScoreboardObjective objective )
    {
        String text = stripStr(player).trim();
        String displayName = objective.getDisplayName().getString().trim();
        if( text.matches(".*Lobby.*") ) {
            UserData.current().setMixelWorld(MuxelWorld.HUB);
        }
        else if ( text.matches("(.*)Rang:(.*)") )
        {
            String rank = Formatting.WHITE + text.replaceAll("• Rang:", "").trim();
            UserData.current().setRank(rank);
        }
        ScoreboardUpdate update = new ScoreboardUpdate(displayName, player, text);
        Client.getInstance().updateTopBar(update);
    }

    @Inject( method = "getPlayerScore", at = @At( "HEAD" ) )
    public void getPlayerScore( String player, ScoreboardObjective objective, CallbackInfoReturnable<ScoreboardPlayerScore> infoReturnable )
    {
        updateObjective(player, objective);
    }

    @Inject( method = "updateExistingObjective", at = @At( "HEAD" ) )
    public void updateExistingObjective( ScoreboardObjective objective, final CallbackInfo callbackInfo )
    {
        if ( objective != null ) updateObjective("", objective);
    }

}
