/*
 *  MIT License
 *
 *   Copyright (c) 2023 Arematics UG (haftungsbeschränkt)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.arematics.enhancements.scoreboard.mappers;

import click.isreal.topbar.Topbar;
import com.arematics.enhancements.domain.MuxelWorld;
import com.arematics.enhancements.domain.ScoreboardMapping;
import com.arematics.enhancements.domain.ScoreboardUpdate;
import com.arematics.enhancements.domain.UserData;
import net.minecraft.util.Formatting;

public class CitybuildMapper implements ScoreboardMapping {
    @Override
    public boolean test(ScoreboardUpdate update) {
        return update.title().matches(".*CityBuild.*");
    }

    @Override
    public void accept(ScoreboardUpdate update) {
        MuxelWorld world = MuxelWorld.findWorld(update.text());
        if(world != MuxelWorld.OTHER) UserData.current().setMixelWorld(world);
        String text = update.text();
        String player = update.rawText();
        if ( text.matches("(.*) ⛀(.*)") )
        {
            String tmpMoney = text.replaceAll("[^0-9,]", "").replaceAll(",", ".");
            try{
                tmpMoney = Topbar.getInstance().moneyformat.format(Double.parseDouble(tmpMoney));
                UserData.current().setMoney(Formatting.YELLOW + tmpMoney);
            }catch (NumberFormatException nfe){
                UserData.current().setMoney(Formatting.GOLD + "<Versteckt>");
            }
        }

        else if ( player.matches("§0§([4-9])§f §8• (.*)") )
        {
            UserData.current().setCbPlotName(player.replaceAll("§0§[4-9]§f §8• ", ""));
        }
        else if ( player.matches("§0§[4-9]§f  §8(.*)") )
        {
            UserData.current().setCbPlotOwner(player.replaceAll("§0§[4-9]§f  §8► ", ""));
        }

        if(player.contains("§0§6§f  §e§owww.MixelPixel.net")){
            UserData.current().setCbPlotName(null);
            UserData.current().setCbPlotOwner(null);
        }
    }
}
