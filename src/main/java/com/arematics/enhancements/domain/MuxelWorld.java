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

package com.arematics.enhancements.domain;

import com.arematics.enhancements.client.Client;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

public enum MuxelWorld {
    HUB(MuxelWorldType.LOBBY, "", Formatting.WHITE),
    SPAWN_1(MuxelWorldType.SPAWN, "1", Formatting.WHITE),
    SPAWN_2(MuxelWorldType.SPAWN, "2", Formatting.WHITE),
    SPAWN_3(MuxelWorldType.SPAWN, "3", Formatting.WHITE),
    SPAWN_4(MuxelWorldType.SPAWN, "4", Formatting.WHITE),
    KFFA(MuxelWorldType.KFFA, "", Formatting.AQUA),
    FARMWORLD_1(MuxelWorldType.FARMWORLD, "1", Formatting.GRAY),
    FARMWORLD_2(MuxelWorldType.FARMWORLD, "2", Formatting.GRAY),
    FARMWORLD_3(MuxelWorldType.FARMWORLD, "3", Formatting.GRAY),
    FARMWORLD_4(MuxelWorldType.FARMWORLD, "4", Formatting.GRAY),
    SMALL_FLORA(MuxelWorldType.SMALL_CB, "Flora", Formatting.GREEN),
    SMALL_AQUA(MuxelWorldType.SMALL_CB, "Aqua", Formatting.AQUA),
    SMALL_VULKAN(MuxelWorldType.SMALL_CB, "Vulkan", Formatting.RED),
    SMALL_DONNER(MuxelWorldType.SMALL_CB, "Donner", Formatting.GOLD),
    BIG_FLORA(MuxelWorldType.BIG_CB, "Flora", Formatting.WHITE),
    BIG_AQUA(MuxelWorldType.BIG_CB, "Aqua", Formatting.WHITE),
    BIG_VULKAN(MuxelWorldType.BIG_CB, "Vulkan", Formatting.WHITE),
    BIG_DONNER(MuxelWorldType.BIG_CB, "Donner", Formatting.WHITE),
    EVENT(MuxelWorldType.EVENT, "", Formatting.WHITE),
    OTHER(MuxelWorldType.OTHER, "", Formatting.RED);

    public static boolean inWorld(){
        return Client.getInstance().isMuxelPixel() &&
                Client.getInstance().getWorld() != MuxelWorld.OTHER;
    }

    public static MuxelWorld findWorld(String text){
        if(StringUtils.isBlank(text)) return OTHER;
        if ( text.matches(".*Flora Klein.*") )
        {
            return MuxelWorld.SMALL_FLORA;
        }
        else if ( text.matches(".*Aqua Klein.*") )
        {
            return MuxelWorld.SMALL_AQUA;
        }
        else if ( text.matches(".*Vulkan Klein.*") )
        {
            return MuxelWorld.SMALL_VULKAN;
        }
        else if ( text.matches(".*Donner Klein.*") )
        {
            return MuxelWorld.SMALL_DONNER;
        }
        else if ( text.matches(".*Flora Groß.*") )
        {
            return MuxelWorld.BIG_FLORA;
        }
        else if ( text.matches(".*Aqua Groß.*") )
        {
            return MuxelWorld.BIG_AQUA;
        }
        else if ( text.matches(".*Vulkan Groß.*") )
        {
            return MuxelWorld.BIG_VULKAN;
        }
        else if ( text.matches(".*Donner Groß.*") )
        {
            return MuxelWorld.BIG_DONNER;
        }
        else if ( text.matches(".*Farmwelt 1.*") )
        {
            return MuxelWorld.FARMWORLD_1;
        }
        else if ( text.matches(".*Farmwelt 2.*") )
        {
            return MuxelWorld.FARMWORLD_2;
        }
        else if ( text.matches(".*Farmwelt 3.*") )
        {
            return MuxelWorld.FARMWORLD_3;
        }
        else if ( text.matches(".*Farmwelt 4.*") )
        {
            return MuxelWorld.FARMWORLD_4;
        }
        else if ( text.matches(".*Spawn 1.*") )
        {
            return MuxelWorld.SPAWN_1;
        }
        else if ( text.matches(".*Spawn 2.*") )
        {
            return MuxelWorld.SPAWN_2;
        }
        else if ( text.matches(".*Spawn 3.*") )
        {
            return MuxelWorld.SPAWN_3;
        }
        else if ( text.matches(".*Spawn 4.*") )
        {
            return MuxelWorld.SPAWN_4;
        }else{
            return OTHER;
        }
    }

    private final MuxelWorldType type;
    private final String subtype;
    private final Formatting formatting;

    MuxelWorld(MuxelWorldType type, String subtype, Formatting formatting){
        this.type = type;
        this.subtype = subtype;
        this.formatting = formatting;
    }

    public MuxelWorldType getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public Formatting getFormatting() {
        return formatting;
    }
}
