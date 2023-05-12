package click.isreal.topbar.events;

import click.isreal.topbar.Topbar;
import click.isreal.topbar.client.TopbarClient;
import click.isreal.topbar.domain.BackpackScanData;
import click.isreal.topbar.domain.SimpleInventoryInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class JobBackpackOpenerCallback implements Consumer<SimpleInventoryInfo> {
    @Override
    public void accept(SimpleInventoryInfo simpleInventory) {
        System.out.println("Open: " + simpleInventory.title());
        if(simpleInventory.title().contains("| Rucksack")){
            check(simpleInventory);
        }
    }

    private void check(SimpleInventoryInfo simpleInventory){
        Topbar.LOGGER.info("Opening Job Backpack");
        String capacity = simpleInventory.inventory().stacks
                .stream()
                .map(JobBackpackOpenerCallback::capacity)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if(capacity == null){
            Topbar.LOGGER.info("Couldn't find Backpack Capacity");
        }else{
            String[] splitted = capacity.replaceAll("»", "")
                    .replaceAll("Items", "")
                    .replaceAll("\\.", "")
                    .trim()
                    .split("/");
            List<ItemStack> items = IntStream.rangeClosed(17, 53)
                    .boxed()
                    .map(i -> simpleInventory.inventory().getStack(i).copy())
                    .toList();
            items.forEach(i -> i.setCount(1));
            BackpackScanData data = new BackpackScanData();
            data.setBackpackName(simpleInventory.title());
            data.setCurrentCapacity(Integer.parseInt(splitted[0]));
            data.setMaxCapacity(Integer.parseInt(splitted[1]));
            data.setCapacityPatching(items);
            TopbarClient.getInstance().scanData = data;
            Topbar.LOGGER.info("Updated Backpack Capacity");
        }
    }

    private static String capacity(ItemStack item) {
        NbtCompound compound = item.getNbt();
        if(compound != null && item.getName().getString().contains("Kapazität")){
            NbtCompound next = compound.getCompound("display");
            if(next != null){
                NbtList list = next.getList("Lore", NbtString.STRING_TYPE);
                if(list != null && list.size() > 0){
                    String string = list.getString(0);
                    Text serialized = Text.Serializer.fromJson(string);
                    if(serialized != null) return serialized.getString();
                }
            }
        }
        return null;
    }
}
