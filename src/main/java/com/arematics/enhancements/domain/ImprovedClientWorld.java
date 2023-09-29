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

import com.arematics.enhancements.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ImprovedClientWorld {
    @Nullable
    private final ClientWorld world;

    public ImprovedClientWorld(@Nullable ClientWorld world){
        this.world = world;
    }

    @Nullable
    public ClientWorld world() {
        return world;
    }

    public boolean isOfType(RegistryKey<World> key){
        return world != null && world.getRegistryKey() == key;
    }

    @Nullable
    public ImprovedSign sign(BlockPos pos){
        if(world == null) return null;
        BlockState state = world.getBlockState(pos);
        BlockEntity entity = world.getBlockEntity(pos);
        if (state != null && entity != null) {
            Item i = state.getBlock().asItem();
            if (ItemHelper.matches(i, "sign") && entity instanceof SignBlockEntity sbe) {
                return new ImprovedSign(sbe, state);
            }
        }
        return null;
    }

    @NotNull
    public <T extends Entity> List<T> getNearPlayer(Class<T> tClass){
        List<T> items = new ArrayList<>();
        if(world == null) return List.of();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) return List.of();
        Vec3d from = player.getPos().subtract(20, 20, 20);
        Vec3d to = player.getPos().add(20, 20, 20);
        return items.getClass().cast(world.getOtherEntities(player, new Box(from, to), (e) -> e.getClass().equals(tClass)));
    }
}
