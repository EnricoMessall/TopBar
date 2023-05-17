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

public class KffaMapper implements ScoreboardMapping {
    @Override
    public boolean test(ScoreboardUpdate update) {
        return update.title().matches(".*KnockbackFFA.*");
    }

    @Override
    public void accept(ScoreboardUpdate update) {
        String trimmedName = update.title()
                .replaceAll("-[0-9]", "").replaceAll("[^0-9\\:]", "");
        UserData.current().setMixelWorld(MuxelWorld.KFFA)
                .setKffaMapSwitch(Formatting.DARK_AQUA + " [" + trimmedName + "]");
        String text = update.text();
        if ( text.matches("(.*)Map:(.*)") )
        {
            String map = Formatting.YELLOW + text.replaceAll("Map:", "").trim();
            UserData.current().setKffaMap(map);
        }
        else if ( text.matches("(.*)Rang:(.*)") )
        {
            String rank = Formatting.YELLOW + text.replaceAll("Rang:", "").trim();
            UserData.current().setRank(rank);
        }
        else if ( text.matches("(.*)Rangpunkte:(.*)") )
        {
            String rankPoints = Formatting.AQUA + "[" + text.replaceAll("[^0-9.]", "").trim() + "/";
            UserData.current().setRankPoints(rankPoints);
        }
        else if ( text.matches("(.*)Aufstieg(.*)") )
        {
            String strAufstiegPoints = Formatting.AQUA + text.replaceAll("[^0-9.]", "").trim() + "]";
            UserData.current().setAufstiegPoints(strAufstiegPoints);
        }
        else if ( text.matches("(.*)Coins:(.*)") )
        {
            String tmpMoney = text.replaceAll("[^0-9,]", "").replaceAll(",", ".");
            try{
                tmpMoney = Topbar.getInstance().moneyformat.format(Double.parseDouble(tmpMoney));
                UserData.current().setMoney(Formatting.GOLD + tmpMoney);
            }catch (NumberFormatException nfe){
                UserData.current().setMoney(Formatting.GOLD + "<Versteckt>");
            }
        }
        else if ( text.matches("(.*)K\\/D:(.*)") )
        {
            String kd = Formatting.YELLOW + text.replaceAll("[^0-9.]", "") + " ⌀";
            UserData.current().setAufstiegPoints(kd);
        }
    }
}
