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

package com.arematics.enhancements.domain.shops;

import click.isreal.topbar.Topbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShopDataCache {

    private static final ShopDataCache INSTANCE = new ShopDataCache();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
    private static final Gson gson = new Gson();

    public static ShopDataCache getInstance(){
        return INSTANCE;
    }
    private Set<Shop> cache = new HashSet<>();

    private void addShop(Shop shop){
        this.cache.add(shop);
    }

    public void addItem(Shop shop, ShopItem shopItem){
        Optional<Shop> cacheItem = this.cache.stream().
                filter(item -> item.shopName().equals(shop.shopName()))
                .findFirst();
        if(cacheItem.isEmpty()){
            shop.items().add(shopItem);
            addShop(shop);
        }else {
            Shop result = cacheItem.get();
            if(result.items().stream().noneMatch(i -> i.hashCode() == shopItem.hashCode())){
                result.items().add(shopItem);
            }
        }
    }

    public void export() throws IOException {
        Topbar.LOGGER.info("Exporting " + cache.size() +" Shops");
        LocalDateTime now = LocalDateTime.now();
        String fileName = "shops/shop_data_export_" + now.format(formatter) + ".json";
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        String json = gson.toJson(new JsonExport(System.currentTimeMillis(), cache));
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(json);
            writer.close();
            this.cache.clear();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static record JsonExport(long timestamp, Set<Shop> shops){}
}
