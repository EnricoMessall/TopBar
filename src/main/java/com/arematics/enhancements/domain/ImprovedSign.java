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

import com.arematics.enhancements.domain.shops.ShopItem;
import com.arematics.enhancements.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Locale;

public class ImprovedSign {
    private static final NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMANY);
    private final SignBlockEntity entity;
    private final BlockState state;

    public ImprovedSign(SignBlockEntity entity, BlockState state){
        this.entity = entity;
        this.state = state;
    }

    public SignBlockEntity entity() {
        return entity;
    }

    public BlockState state() {
        return state;
    }

    public String getText(int line){
        Text text = entity.getTextOnRow(line, false);
        if(text == null) return "";
        return text.getString();
    }

    @Nullable
    public Text getPlain(int line){
        return entity.getTextOnRow(line, false);
    }

    @Nullable
    public ShopItem toMixelShopItem(ItemStack frameItem){
        Identifier identifier = Registries.ITEM.getId(frameItem.getItem());
        Text isShop = getPlain(0);
        if(isShop == null || !isShop.getString().contains("Usershop")) return null;
        boolean empty = isShop.toString().contains("color=red");
        int amount = 0;
        double price = 0.0;
        try{
            String amountText = getText(2).replaceAll("\\.", "").replaceAll("Stück", "").trim();
            amount = format.parse(amountText).intValue();
            String priceText = getText(3).replaceAll("\\.", "").replaceAll("⛀", "").trim();
            price = format.parse(priceText).doubleValue();
        }catch (Exception e){
            return null;
        }
        String enchant = null;
        try{
            if(ItemHelper.matches(frameItem, "enchanted_book")){
                NbtList enchantments = EnchantedBookItem.getEnchantmentNbt(frameItem);
                if(enchantments != null && enchantments.size() > 0) {
                    NbtCompound nbtItem = enchantments.getCompound(0);
                    Identifier enchantmentId = Identifier.tryParse(nbtItem.getString("id"));
                    Short level = nbtItem.getShort("lvl");
                    if(enchantmentId != null){
                        Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentId);
                        if(enchantment != null){
                            enchant = enchantment.getName(level).getString();
                        }
                    }
                }
            }
        }catch (Exception ignore){}
        return new ShopItem(identifier.toString(), frameItem.getName().getString(), amount, price, empty, enchant);
    }

    @Override
    public String toString() {
        return "ImprovedSign{" +
                "entity=" + entity +
                ", state=" + state +
                '}';
    }
}
